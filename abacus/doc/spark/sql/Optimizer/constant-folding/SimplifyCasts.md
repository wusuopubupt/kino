### 简化类型转换(Simplify Casts)


* 简化Cast，如果数据类型和要转换的类型一致，则去掉Cast

例如sql:

```sql
select cast(name as String) from t1
```
优化过程：

```sql
// name本身就是String类型
scala> sqlContext.sql("select cast(name as String) from t1")
17/07/25 16:59:44 INFO parse.ParseDriver: Parsing command: select cast(name as String) from t1
17/07/25 16:59:44 INFO parse.ParseDriver: Parse Completed
res29: org.apache.spark.sql.DataFrame = [name: string]

scala> res29.queryExecution
res30: org.apache.spark.sql.execution.QueryExecution =
== Parsed Logical Plan ==
'Project [unresolvedalias(cast('name as string))]
+- 'UnresolvedRelation `t1`, None

== Analyzed Logical Plan ==
name: string
Project [cast(name#5 as string) AS name#20]
+- Subquery t1
   +- Project [_1#0 AS name#5,_2#1 AS date#6,_3#2 AS cate#7,_4#3 AS amountSpent#8,_5#4 AS time#9]
      +- LogicalRDD [_1#0,_2#1,_3#2,_4#3,_5#4], MapPartitionsRDD[1] at rddToDataFrameHolder at <console>:27

== Optimized Logical Plan ==
// 去掉了无用的cast
Project [_1#0 AS name#20]
+- LogicalRDD [_1#0,_2#1,_3#2,_4#3,_5#4], MapPartitionsRDD[1] at rddToDataFrameHolder at <console>:27

== Physical Plan ==
Project [_1#0 AS name#20]
+- Scan ExistingRDD[_1#0,_2#1,_3#2,_4#3,_5#4]
```
由于name字段本身就是String类型，和cast的目标类型是一样的，所以cast语句在逻辑计划被去掉了

* 源码如下：

```scala
/**
  * 简化Cast，如果数据类型和要转换的类型一致，则去掉Cast
  */
object SimplifyCasts extends Rule[LogicalPlan] {
  def apply(plan: LogicalPlan): LogicalPlan = plan transformAllExpressions {
    case Cast(e, dataType) if e.dataType == dataType => e
  }
}
```