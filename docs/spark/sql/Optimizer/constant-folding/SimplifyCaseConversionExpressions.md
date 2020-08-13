### 简化大小写转化表达式(Simplify Case Conversion Expressions)

* 对于嵌套大小写转化表达式，以最外层为准，去掉里层的转化表达式

例如，sql语句为:

```sql
select upper(lower(name)) from t1
```
优化过程：

```sql
scala> sqlContext.sql("select upper(lower(name)) from t1")
17/07/25 17:13:01 INFO parse.ParseDriver: Parsing command: select upper(lower(name)) from t1
17/07/25 17:13:01 INFO parse.ParseDriver: Parse Completed
res34: org.apache.spark.sql.DataFrame = [_c0: string]

scala> res34.queryExecution
res35: org.apache.spark.sql.execution.QueryExecution =
== Parsed Logical Plan ==
'Project [unresolvedalias('upper('lower('name)))]
+- 'UnresolvedRelation `t1`, None

== Analyzed Logical Plan ==
_c0: string
Project [upper(lower(name#5)) AS _c0#22]
+- Subquery t1
   +- Project [_1#0 AS name#5,_2#1 AS date#6,_3#2 AS cate#7,_4#3 AS amountSpent#8,_5#4 AS time#9]
      +- LogicalRDD [_1#0,_2#1,_3#2,_4#3,_5#4], MapPartitionsRDD[1] at rddToDataFrameHolder at <console>:27

== Optimized Logical Plan ==
Project [upper(_1#0) AS _c0#22]
+- LogicalRDD [_1#0,_2#1,_3#2,_4#3,_5#4], MapPartitionsRDD[1] at rddToDataFrameHolder at <console>:27

== Physical Plan ==
Project [upper(_1#0) AS _c0#22]
+- Scan ExistingRDD[_1#0,_2#1,_3#2,_4#3,_5#4]
```

相当于执行:

```sql
select upper(name) from t1
```


* 源码如下：

```scala
object SimplifyCaseConversionExpressions extends Rule[LogicalPlan] {
  def apply(plan: LogicalPlan): LogicalPlan = plan transform {
    case q: LogicalPlan => q transformExpressionsUp {
      // 以最外层转化表达式为准,其余删掉
      case Upper(Upper(child)) => Upper(child)
      case Upper(Lower(child)) => Upper(child)
      case Lower(Upper(child)) => Lower(child)
      case Lower(Lower(child)) => Lower(child)
    }
  }
}

```

