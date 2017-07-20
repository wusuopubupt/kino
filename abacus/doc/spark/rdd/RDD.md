# Spark RDD Scala语言编程

------

**RDD**（Resilient Distributed Dataset）是一个不可变的分布式对象集合， 每个rdd被分为多个分区， 这些分区运行在集群的不同节点上。rdd支持两种类型的操作：**转化**(trainsformation)和**行动**(action)， Spark只会**惰性**计算rdd, 也就是说， 转化操作的rdd不会立即计算， 而是在其第一次遇到行动操作时才去计算， 如果想在多个行动操作中复用一个rdd, 可以使用RDD.persist()让Spark把这个rdd缓存下来。

### 0. 初始化SparkContext
```scala
import org.apache.spark.{SparkConf, SparkContext}

val sc = new SparkContext(new SparkConf().setMaster("local[*]").setAppName("spark-rdd-demo"))
```

### 1. 创建RDD
Spark提供了2种创建RDD的方式：
##### 1.1 读取外部数据集
``` scala
val lines = sc.parallelize(List("Java", "Scala", "C++"))
```
##### 1.2 在驱动器程序中对一个集合进行并行化
``` scala
val lines = sc.textFile("hdfs://dash-dev:9000/input/test.txt")
```

### 2. RDD操作
##### 2.1 转化操作
RDD的转化操作是返回新RDD的操作, 常用转化操作总结如下：

**表1: 对一个数据为{1,2,3,3}的RDD进行基本的转化操作**

| 函数名     | 目的     |  示例   | 结果 |
| --------  | -----:  | :----:  | --: |
| map()     | 将函数应用于RDD中每个元素， 将返回值构成新的RDD| rdd.map(x=>x+1) | {2,3,4,5}
| flatMap() |   将函数应用于RDD中的每个元素， 将返回的迭代器的所有内容构成新的RDD， 常用来切分单词  | rdd.flatMap(x=>x.to(2))  | {1,2,2}|
| filter()  | 返回一个通过传入给filter()的函数的元素组成的RDD   |  rdd.filter(x=> x>2)  | {3,3}|
| distinct()| 去重 | rdd.distinct()  |{1,2,3} |
| sample(withReplacement, fraction, [seed])  |    对RDD采样， 以及是否替换  |  rdd.sample(false, 0.5)  | 非确定的|

**表2: 对数据分别为{1,2,3}和{2,3,4}RDD进行针对2个RDD的转化操作**

| 函数名     | 目的     |  示例   | 结果 |
| --------  | -----:  | :----:  | --: |
| union()    | 求2个RDD的并集| rdd.union(other)| {1,2,3,4}
| intersection() | 求2个RDD的交集 | rdd.intersection(other)  | {2,3}|
| subtract()  | 求2个RDD的差集   | rdd.subtract(other)  | {1}|
| cartesian() | 求2个RDD的笛卡尔积 | rdd.cartesian(other)  |{1,2}, {1,3}, {1,4}...{3,4} |
| sample(withReplacement, fraction, [seed])  |    对RDD采样， 以及是否替换  |  rdd.sample(false, 0.5)  | 非确定的|

##### 2.2 行动操作
RDD的行动操作会把最终求得的结果返回驱动器程序， 或者写入外部存储系统中。

**表3: 对一个数据为{1,2,3,3}的RDD进行基本RDD的行动操作**

| 函数名     | 目的     |  示例   | 结果 |
| --------  | -----:  | :----:  | --: |
| redcue()  | 并行整合RDD中的所有元素| rdd.reduce((x, y) => x+y) |9 |
| collect() | 返回RDD中的所有元素| rdd.collect()| {1,2,3,4}
| count() | 求RDD中的元素个数 | rdd.count()  | 4|
| countByValue()  | 各元素在RDD中出现的次数   | rdd.countByValue()  | {1,1}, {2, 1}, {3,2}|
| take(n) | 从RDD中返回n个元素 | rdd.take(2)  |{1,2}|
| top(n)|  从RDD中返回前n个元素 |  rdd.top(3)  | {3,3,2}|
| foreach(func)|  对RDD中的每个元素使用给定的函数|  rdd.foreach(print)  | 1,2,3,3|

##### 2.3 向Spark传递函数
Spark的大部分转化和行动操作都要依赖于用户传递的函数来计算， 当传递的对象是某个对象的成员， 或者包含了对某个对象中一个字段的引用时(如self.field), Spark就会把**整个对象**发送到工作节点上－－这比你本意想传递的东西大太多了！替代的方案是，把你需要的字段从对象中拿出来放到一个局部变量中， 然后传递这个局部变量：

```scala
class SearchFunctions(val query: String) {
	def isMatch(s: String): Boolean = {
		s.contains(query)
	}
	
	def getMatchesFunctionReference(rdd: RDD[String]): RDD[String] = {
		// 问题："isMatch"表示"this.isMatch", 因此会传递整个this
		rdd.map(isMatch)
	}
	
	def getMatchesFieldReference(rdd: RDD[String]): RDD[String] = {
		// 问题： "query"表示"this.query", 因此会传递整个this
		rdd.map(x => x.split(query))
	}
	
	def getMatchesNoReference(rdd: RDD[String]): RDD[String] = {
		// 安全：只把我们需要的字段拿出来放入局部变量中
		val localQuery = this.query
		rdd.map(x => x.split(localQuery))
	}
}
```
另外， 要注意的是， Spark要求我们传入的函数及其应用的数据是可序列化的(实现了Java的Serializable接口)， 否则会出现NotSerializableException。



作者 [@wusuopubupt][1]   
2016年11月11日    

[1]: http://github.com/wusuopubupt


