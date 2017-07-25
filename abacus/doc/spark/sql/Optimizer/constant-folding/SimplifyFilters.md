### 简化过滤器(Simplify Filters)

#### 
* 如果过滤器一直返回true， 则删掉此过滤器(如：where 2>1)
* 如果过滤器一直返回false, 则直接让计划返回空(如： where 2<1)


**例一：**

sql语句为:

```sql
select name from t1 where 2 > 1
```
优化过程：
2 > 1恒为true， 删掉此过滤器(见Optimized Plan)

```sql
scala> sqlContext.sql("select name from t1 where 2 > 1")
17/07/25 15:50:25 INFO parse.ParseDriver: Parsing command: select name from t1 where 2 > 1
17/07/25 15:50:25 INFO parse.ParseDriver: Parse Completed
res23: org.apache.spark.sql.DataFrame = [name: string]

scala> res23.queryExecution
res24: org.apache.spark.sql.execution.QueryExecution =
== Parsed Logical Plan ==
'Project [unresolvedalias('name)]
+- 'Filter (2 > 1)
   +- 'UnresolvedRelation `t1`, None

== Analyzed Logical Plan ==
name: string
Project [name#5]
+- Filter (2 > 1)
   +- Subquery t1
      +- Project [_1#0 AS name#5,_2#1 AS date#6,_3#2 AS cate#7,_4#3 AS amountSpent#8,_5#4 AS time#9]
         +- LogicalRDD [_1#0,_2#1,_3#2,_4#3,_5#4], MapPartitionsRDD[1] at rddToDataFrameHolder at <console>:27

== Optimized Logical Plan ==
Project [_1#0 AS name#5]
+- LogicalRDD [_1#0,_2#1,_3#2,_4#3,_5#4], MapPartitionsRDD[1] at rddToDataFrameHolder at <console>:27

== Physical Plan ==
Project [_1#0 AS name#5]
+- Scan ExistingRDD[_1#0,_2#1,_3#2,_4#3,_5#4]
```

相当于执行:

```sql
select name from t1
```

**例二：**

sql语句为:

```sql
select name from t1 where 2 < 1
```
优化过程：
2 < 1恒为false， 直接返回空

```sql
scala> sqlContext.sql("select name from t1 where 2 < 1")
17/07/25 15:54:46 INFO parse.ParseDriver: Parsing command: select name from t1 where 2 < 1
17/07/25 15:54:46 INFO parse.ParseDriver: Parse Completed
res25: org.apache.spark.sql.DataFrame = [name: string]

scala> res25.queryExecution
res26: org.apache.spark.sql.execution.QueryExecution =
== Parsed Logical Plan ==
'Project [unresolvedalias('name)]
+- 'Filter (2 < 1)
   +- 'UnresolvedRelation `t1`, None

== Analyzed Logical Plan ==
name: string
Project [name#5]
+- Filter (2 < 1)
   +- Subquery t1
      +- Project [_1#0 AS name#5,_2#1 AS date#6,_3#2 AS cate#7,_4#3 AS amountSpent#8,_5#4 AS time#9]
         +- LogicalRDD [_1#0,_2#1,_3#2,_4#3,_5#4], MapPartitionsRDD[1] at rddToDataFrameHolder at <console>:27

== Optimized Logical Plan ==
LocalRelation [name#5]

== Physical Plan ==
LocalTableScan [name#5]
```


* 源码如下：

```scala

object SimplifyFilters extends Rule[LogicalPlan] {
  def apply(plan: LogicalPlan): LogicalPlan = plan transform {
    // If the filter condition always evaluate to true, remove the filter.
    case Filter(Literal(true, BooleanType), child) => child
    // If the filter condition always evaluate to null or false,
    // replace the input with an empty relation.
    case Filter(Literal(null, _), child) => LocalRelation(child.output, data = Seq.empty)
    case Filter(Literal(false, BooleanType), child) => LocalRelation(child.output, data = Seq.empty)
  }
}

```

