### Null值表达式替换(Null Propagation)


* 在某些特定场景下替换null表达式为字面量，阻止NULL表达式传播


**例一：**

如sql:

```sql
select count(null) from t1
```
优化过程：

```sql
scala> sqlContext.sql("select count(null) from t1")
17/07/26 11:40:18 INFO parse.ParseDriver: Parsing command: select count(null) from t1
17/07/26 11:40:18 INFO parse.ParseDriver: Parse Completed
res8: org.apache.spark.sql.DataFrame = [_c0: bigint]

scala> res8.queryExecution
res10: org.apache.spark.sql.execution.QueryExecution =
== Parsed Logical Plan ==
'Project [unresolvedalias('count(null))]
+- 'UnresolvedRelation `t1`, None

== Analyzed Logical Plan ==
_c0: bigint
Aggregate [(count(null),mode=Complete,isDistinct=false) AS _c0#10L]
+- Subquery t1
   +- Project [_1#0 AS name#5,_2#1 AS date#6,_3#2 AS cate#7,_4#3 AS amountSpent#8,_5#4 AS time#9]
      +- LogicalRDD [_1#0,_2#1,_3#2,_4#3,_5#4], MapPartitionsRDD[1] at rddToDataFrameHolder at <console>:27

== Optimized Logical Plan ==
// 直接返回0
Aggregate [0 AS _c0#10L]
+- Project
   +- LogicalRDD [_1#0,_2#1,_3#2,_4#3,_5#4], MapPartitionsRDD[1] at rddToDataFrameHolder at <console>:27
```
可见经过优化后，逻辑计划里的对count(null)优化后直接返回0

**例二：**

如sql(这里time类型不允许为null):

```sql
select isNull(time) from t1

```

执行结果：

```sql
scala> res16.show

+-----+
|  _c0|
+-----+
|false|
|false|
|false|
|false|
|false|
|false|
|false|
|false|
|false|
|false|
|false|
|false|
|false|
|false|
|false|
|false|
|false|
|false|
|false|
|false|
+-----+
only showing top 20 rows
```

优化过程：

```sql
scala> res16.queryExecution
res18: org.apache.spark.sql.execution.QueryExecution =
== Parsed Logical Plan ==
'Project [unresolvedalias('isNull('time))]
+- 'UnresolvedRelation `t1`, None

== Analyzed Logical Plan ==
_c0: boolean
Project [isnull(time#9) AS _c0#12]
+- Subquery t1
   +- Project [_1#0 AS name#5,_2#1 AS date#6,_3#2 AS cate#7,_4#3 AS amountSpent#8,_5#4 AS time#9]
      +- LogicalRDD [_1#0,_2#1,_3#2,_4#3,_5#4], MapPartitionsRDD[1] at rddToDataFrameHolder at <console>:27

== Optimized Logical Plan ==
Project [false AS _c0#12]
+- LogicalRDD [_1#0,_2#1,_3#2,_4#3,_5#4], MapPartitionsRDD[1] at rddToDataFrameHolder at <console>:27

== Physical Plan ==
Project [false AS _c0#12]
+- Scan ExistingRDD[_1#0,_2#1,_3#2,_4#3,_5#4]
```
可见经过优化后，逻辑计划里的对isNull(time)优化后直接每行都返回false, 不必扫描全表

* 源码如下：

```scala
object NullPropagation extends Rule[LogicalPlan] {  
  def apply(plan: LogicalPlan): LogicalPlan = plan transform {  
    case q: LogicalPlan => q transformExpressionsUp {  
case e @ Count(Literal(null, _)) => Cast(Literal(0L), e.dataType)//如果count(null)则转化为count(0)  
case e@AggregateExpression(Count(exprs), _, _) if !exprs.exists(nonNullLiteral) =>
        Cast(Literal(0L), e.dataType)
    case e@IsNull(c) if !c.nullable => Literal.create(false, BooleanType)
    case e@IsNotNull(c) if !c.nullable => Literal.create(true, BooleanType)
    case ... 
} 

```
