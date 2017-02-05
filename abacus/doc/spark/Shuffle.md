spark实现了多种shuffle方法，通过 spark.shuffle.manager来确定, shuffle策略有三种：
- hash shuffle
- sort shuffle
- tungsten-sort shuffle

从1.2.0开始默认为sort shuffle


### Hash Shuffle
spark在1.2前默认为hash shuffle（spark.shuffle.manager = hash）
![](http://spark-internals.books.yourtion.com/markdown/PNGfigures/shuffle-write-no-consolidation.png)

上图有 4 个 ShuffleMapTask 要在同一个 worker node 上运行，CPU core 数为 2，可以同时运行两个 task。每个 task 的执行结果（该 stage 的 finalRDD 中某个 partition 包含的 records）被逐一写到本地磁盘上。每个 task 包含 R 个缓冲区，R = reducer 个数（也就是下一个 stage 中 task 的个数），缓冲区被称为 bucket，其大小为spark.shuffle.file.buffer.kb ，默认是 32KB（Spark 1.1 版本以前是 100KB）。

这样的实现很简单，但有几个问题：

1 产生的 *FileSegment* 过多。每个 ShuffleMapTask 产生 R（reducer 个数）个 FileSegment，M 个 ShuffleMapTask 就会产生 `M * R` 个文件。一般 Spark job 的 M 和 R 都很大，因此磁盘上会存在大量的数据文件。

2 缓冲区占用内存空间大。每个 ShuffleMapTask 需要开 R 个 bucket，M 个 ShuffleMapTask 就会产生 M \* R 个 bucket。虽然一个 ShuffleMapTask 结束后，对应的缓冲区可以被回收，但一个 worker node 上同时存在的 bucket 个数可以达到 cores R 个（一般 worker 同时可以运行 cores 个 ShuffleMapTask），占用的内存空间也就达到了**cores \* R \* 32 KB**。对于 8 核 1000 个 reducer 来说，占用内存就是 256MB。

spark.shuffle.consolidateFiles默认为false，如果为true，shuffleMapTask输出文件可以被合并。如图

![](http://spark-internals.books.yourtion.com/markdown/PNGfigures/shuffle-write-consolidation.png)

可以明显看出，在一个 core 上连续执行的 ShuffleMapTasks 可以共用一个输出文件 ShuffleFile。先执行完的 ShuffleMapTask 形成 ShuffleBlock i，后执行的 ShuffleMapTask 可以将输出数据直接追加到 ShuffleBlock i 后面，形成 ShuffleBlock i'，每个 ShuffleBlock 被称为 FileSegment。下一个 stage 的 reducer 只需要 fetch 整个 ShuffleFile 就行了。这样，每个 worker 持有的文件数降为 `cores * R`。**但是缓存空间占用大还没有解决**。

#### 总结

**优点**

1. 快-不需要排序，也不需要维持hash表
2. 不需要额外空间用作排序
3. 不需要额外IO-数据写入磁盘只需一次，读取也只需一次

**缺点**

1. 当partitions大时，输出大量的文件（cores * R）,性能开始降低
2. 大量的文件写入，使文件系统开始变为随机写，性能比顺序写要降低100倍
3. 缓存空间占用比较大


### Sort Shuffle
从1.2.0开始默认为sort shuffle(**spark.shuffle.manager** = sort)，实现逻辑类似于Hadoop MapReduce，Hash Shuffle每一个reducers产生一个文件，但是Sort Shuffle只是产生一个按照reducer id排序可索引的文件，这样，只需获取有关文件中的相关数据块的位置信息，并fseek就可以读取指定reducer的数据。但对于rueducer数比较少的情况，Hash Shuffle明显要比Sort Shuffle快，因此Sort Shuffle有个“fallback”计划，对于reducers数少于 “spark.shuffle.sort.bypassMergeThreshold” (200 by default)，我们使用fallback计划，hashing相关数据到分开的文件，然后合并这些文件为一个，具体实现为[BypassMergeSortShuffleWriter](https://github.com/apache/spark/blob/master/core/src/main/java/org/apache/spark/shuffle/sort/BypassMergeSortShuffleWriter.java)。

![image](https://raw.githubusercontent.com/jacksu/utils4s/master/spark-knowledge/images/spark_sort_shuffle.png)

在map进行排序，在reduce端应用Timsort[1]进行合并。map端是否容许spill，通过**spark.shuffle.spill**来设置，默认是true。设置为false，如果没有足够的内存来存储map的输出，那么就会导致OOM错误，因此要慎用。

用于存储map输出的内存为：`“JVM Heap Size” \* spark.shuffle.memoryFraction \* spark.shuffle.safetyFraction`，默认为`“JVM Heap Size” \* 0.2 \* 0.8 = “JVM Heap Size” \* 0.16`。如果你在同一个执行程序中运行多个线程（设定`spark.executor.cores/ spark.task.cpus`超过1）,每个map任务存储的空间为`“JVM Heap Size” * spark.shuffle.memoryFraction * spark.shuffle.safetyFraction / spark.executor.cores * spark.task.cpus`, 默认2个cores，那么为`0.08 * “JVM Heap Size”`。
spark使用[AppendOnlyMap](nch-1.5/core/src/main/scala/org/apache/spark/util/collection/AppendOnlyMap.scala)存储map输出的数据，利用开源hash函数[MurmurHash3](https://zh.wikipedia.org/wiki/Murmur哈希)和平方探测法把key和value保存在相同的array中。这种保存方法可以是spark进行combine。如果spill为true，会在spill前sort。


**优点**
1. map创建文件量较少
2. 少量的IO随机操作，大部分是顺序读写

**缺点**
1. 要比Hash Shuffle要慢，需要自己通过`spark.shuffle.sort.bypassMergeThreshold`来设置合适的值。
2. 如果使用SSD盘存储shuffle数据，那么Hash Shuffle可能更合适。

### Tungsten-sort shuffle Shuffle
Tungsten-sort优化点主要在三个方面:

1. 直接在serialized binary data上sort而不是java objects，减少了memory的开销和GC的overhead。
2. 提供cache-efficient sorter，使用一个8bytes的指针，把排序转化成了一个指针数组的排序。
3. spill的merge过程也无需反序列化即可完成

