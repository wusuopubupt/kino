### 合并字段 (Project Collapsing)
* 把2个相邻的Project（字段）操作符合并成1个，同时替换别名

例如sql:
 
```sql 
select name, c+1 from (select name, time, time+amountSpent as c from t1 where time > 1) tmp_t where time > 8
```

优化为：

```sql
scala> sqlContext.sql("select name, c+1 from (select name, time, time+amountSpent as c from t1 where time > 1) tmp_t where time > 8")
17/07/25 15:30:43 INFO parse.ParseDriver: Parsing command: select name, c+1 from (select name, time, time+amountSpent as c from t1 where time > 1) tmp_t where time > 8
17/07/25 15:30:43 INFO parse.ParseDriver: Parse Completed
res21: org.apache.spark.sql.DataFrame = [name: string, _c1: int]

scala> res21.queryExecution.optimizedPlan
res22: org.apache.spark.sql.catalyst.plans.logical.LogicalPlan =
Project [_1#0 AS name#5,((_5#4 + _4#3) + 1) AS _c1#18]
+- Filter ((_5#4 > 1) && (_5#4 > 8))
   +- LogicalRDD [_1#0,_2#1,_3#2,_4#3,_5#4], MapPartitionsRDD[1] at rddToDataFrameHolder at <console>:27
```

相当于执行:

```sql
select name, time+amountSpent+1 from t1 where time > 8

```

* 源码如下：

```scala
/**
  * 把2个相邻的Project（字段）操作符合并成1个，同时替换别名
  */
object ProjectCollapsing extends Rule[LogicalPlan] {

  def apply(plan: LogicalPlan): LogicalPlan = plan transformUp {
    case p@Project(projectList1, Project(projectList2, child)) =>
      // 根据子查询构造别名->真实值的映射关系.
      // e.g., 'SELECT ... FROM (SELECT a + b AS c, d ...)' produces Map(c -> Alias(a + b, c)).
      val aliasMap = AttributeMap(projectList2.collect {
        case a: Alias => (a.toAttribute, a)
      })

      // We only collapse these two Projects if their overlapped expressions are all
      // deterministic.
      // 只对重叠表达式都是确定性的情况做project合并?(不懂啥意思)
      val hasNondeterministic = projectList1.exists(_.collect {
        case a: Attribute if aliasMap.contains(a) => aliasMap(a).child
      }.exists(!_.deterministic))

      if (hasNondeterministic) {
        p
      } else {
        // 根据子查询构造出来的别名->真实值的映射关系替换别名
        // e.g., 'SELECT c + 1 FROM (SELECT a + b AS C ...' produces 'SELECT a + b + 1 ...'
        val substitutedProjection = projectList1.map(_.transform {
          case a: Attribute => aliasMap.getOrElse(a, a)
        }).asInstanceOf[Seq[NamedExpression]]
        // 消除上面getOrElse操作可能引入的没有必要的别名
        val cleanedProjection = substitutedProjection.map(p =>
          CleanupAliases.trimNonTopLevelAliases(p).asInstanceOf[NamedExpression]
        )
        Project(cleanedProjection, child)
      }
  }
}
```