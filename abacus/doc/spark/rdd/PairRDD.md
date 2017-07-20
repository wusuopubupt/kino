###Spark Pair RDD操作

#### 1. 创建Pair RDD
```scala
val pairs = lines.map(x => (x.split(" ")(0), x)
```
#### 2. Pair RDD的转化方法


**表1 Pair RDD的转化方法(以键值对集合{(1,2), (3,4), (3, 6)}为例)**

| 函数名     | 目的     |  示例   | 结果 |
| --------  | -----:  | :----:  | --: |
| reduceByKey() | 合并具有相同键的值| rdd.reduceByKey((x,y)=>x+y)| {(1,2), (3,10)}
| groupByKey() | 对具有相同键的值进行分组 | rdd.groupByKey()  | {(1,[2]), (3, [4, 6])}|
| mapValues()  | 对Pair RDD中的每个值应用而不改变键 | rdd.mapValues(x=>x+1)  | {(1,3),(3,5),(3,7)}|
| keys() |返回一个仅包含键的RDD | rdd.keys  |{1,3,3} |
| values()  |   返回一个仅包含值的RDD |  rdd.values  | {2,4,6}|
| sortByKey()  | 返回一个根据键排序的RDD|  rdd.sortByKey()  | {(1,2), (3,4), (3, 6)}|

**表2 针对两个pair RDD的转化操作 (rdd={1,2},{3,4},{3,6}, other={(3,9)})**

| 函数名     | 目的     |  示例   | 结果 |
| --------  | -----:  | :----:  | --: |
| subtractBykey | 删掉RDD中与other RDD中键相同的元素| rdd.subtractByKey(other)| {(1,2)}
| join | 对两个RDD进行内连接| rdd.join(other)  | {(3,(4,9)), (3, (6,9))}|
| leftOuterJoin|左外连接， 确保第一个RDD的键必须存在| rdd.leftOuterJoin(other)  |{(1, (2, None)),(3, (4, Some(9)),(3, (6, Some(9Ish							)))} |
| rightOuterJoin  |右外连接， 确保第二个RDD的键必须存在|rdd.rightOuterJoin(other) |{(3, (Some(4), 9)),(3,(Some(6), 9))}|
| cogroup  |  将两个RDD具有相同键的数据分组到一起 |  rdd.cogroup(other)  | {(1, ([2],[])), (3, ([4,6], [9]))}|


#### 2.1 聚合操作
用combineByKey()求每个键对应平均值的数据流示意图：

**分区1**                

 key    | value|
| ------| -----|  
|coffee | 1| 
|coffee | 2|              
|panda | 3| 

**分区2**                

 key    | value|
| ------| -----|  
|coffee | 9| 

 
***处理分区1的过程：***

```scala
(coffee, 1) -> new key

accumulators[coffee] = createCombiner(1)

(coffee, 2) -> existing key

accululators[coffer] = mergeValue(accumulators[coffee], 2)

(panda, 3) -> new key

accumulators[panda] = createCombiner(3)
```
 
***处理分区2的过程：***

```scala
(coffee, 9) -> new key

accumulators[coffee] = createCombiner(9)
```
***合并分区：***

```scala
mergeConbiners(partation1.accumulators[coffee], partation2.accumulators[coffee])
```

以上用到的函数如下：

```scala
def createCombiner(value): (value, 1)

def mergeValue(accumulator, value): (accumulator[0]+value, accumulator[1]+1)

def mergeCombiners(accumulator1, accumulator2): (accumulator1[0]+accumulator2[0], accumulator1[1]+accumulator2[1]
```



#### 2.2 数据分组
groupBykey()可以对RDD相同键的数据进行分组。对于一个类型K的键和一个类型V的值组成的RDD， groupByKey()操作返回的RDD结果是[K, Iterable[V]]。

注意：

rdd.reduceByKey(func)与rdd.groupByKey().mapValues(values=> value.reduce(func))等价， 但**前者更为高效**。



#### 3. Pair RDD的行动方法
**表3 Pair RDD的行动操作（以键值对集合{(1,2), (3,4), (3,6)}为例）**


|函数名| 描述| 示例|结果|
|---|--:|:--:|--:|
|countByKey() | 对每个键对应的元素分别计数|rdd.countByKey() | {(1,1), {3,2}}|
|collectAsMap() | 将结果以映射表的形式返回 | rdd.collectAsMap() | {(1,2), (3,6)} |
|lookup(key) | 返回给定键对应的所有值|rdd.lookup(3)| [4,6]|



