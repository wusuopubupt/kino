### 合并相邻的过滤器(Combine Filters)
* 合并相邻的过滤条件

例如sql：

```sql 
select name from (select name, time from t1 where time > 1) tmp_t where time > 8
```

优化后的筛选条件变为: time > 1 && time > 8, 即 time > 8
    

```sql
scala> sqlContext.sql("select name from (select name, time from t1 where time > 1) tmp_t where time > 8")
17/07/25 14:31:38 INFO parse.ParseDriver: Parsing command: select name from (select name, time from t1 where time > 1) tmp_t where time > 8
17/07/25 14:31:39 INFO parse.ParseDriver: Parse Completed
res1: org.apache.spark.sql.DataFrame = [name: string]

scala> res1.queryExecution.optimizedPlan
res2: org.apache.spark.sql.catalyst.plans.logical.LogicalPlan =
Project [_1#0 AS name#5]
+- Filter ((_5#4 > 1) && (_5#4 > 8))
   +- LogicalRDD [_1#0,_2#1,_3#2,_4#3,_5#4], MapPartitionsRDD[1] at rddToDataFrameHolder at <console>:27

```

相当于执行：

```sql
select name from t1 where time > 8

```

* 源码如下：

```scala
object CombineFilters extends Rule[LogicalPlan] {
  def apply(plan: LogicalPlan): LogicalPlan = plan transform {
    case ff@Filter(fc, nf@Filter(nc, grandChild)) => Filter(And(nc, fc), grandChild) // 合并为&&筛选条件
  }
}
```