### 下推过滤器至join的左边和右边(Push Predicate Through Join)


* 下推过滤器至join的左边和右边

**例一：不带sub query的Inner Join**

sql:

```sql
select t1.name, t2.name from t1 inner join t2 on t1.time=t2.time and t1.time > 0 and t2.time > 8
```
优化过程：

```sql
scala> sqlContext.sql("select t1.name, t2.name from t1 inner join t2 on t1.time=t2.time and t1.time > 0 and t2.time > 8")
17/07/27 18:06:38 INFO parse.ParseDriver: Parsing command: select t1.name, t2.name from t1 inner join t2 on t1.time=t2.time and t1.time > 0 and t2.time > 8
17/07/27 18:06:38 INFO parse.ParseDriver: Parse Completed
res75: org.apache.spark.sql.DataFrame = [name: string, name: string]

scala> res75.queryExecution.optimizedPlan
res76: org.apache.spark.sql.catalyst.plans.logical.LogicalPlan =
Project [name#5,name#184]
+- Join Inner, Some((time#9 = time#188))
   :- Project [_1#0 AS name#5,_5#4 AS time#9]
   :  +- Filter (_5#4 > 0)
   :     +- LogicalRDD [_1#0,_2#1,_3#2,_4#3,_5#4], MapPartitionsRDD[1] at rddToDataFrameHolder at <console>:27
   +- Project [_1#0 AS name#184,_5#4 AS time#188]
      +- Filter (_5#4 > 8)
         +- LogicalRDD [_1#0,_2#1,_3#2,_4#3,_5#4], MapPartitionsRDD[1] at rddToDataFrameHolder at <console>:27
```
可见经过优化后，逻辑计划会先对t1和t2表执行filter操作，再做Join

**例二：不带sub query的LeftOuter Join**

sql:

```sql
select t1.name, t2.name from t1 left join t2 on t1.time=t2.time and t1.time > 0 and t2.time > 8
```
优化过程：

```sql
scala> sqlContext.sql("select t1.name, t2.name from t1 left join t2 on t1.time=t2.time and t1.time > 0 and t2.time > 8")
17/07/27 18:05:02 INFO parse.ParseDriver: Parsing command: select t1.name, t2.name from t1 left join t2 on t1.time=t2.time and t1.time > 0 and t2.time > 8
17/07/27 18:05:02 INFO parse.ParseDriver: Parse Completed
res71: org.apache.spark.sql.DataFrame = [name: string, name: string]

scala> res71.queryExecution.optimizedPlan
res72: org.apache.spark.sql.catalyst.plans.logical.LogicalPlan =
Project [name#5,name#174]
+- Join LeftOuter, Some(((time#9 > 0) && (time#9 = time#178)))
   :- Project [_1#0 AS name#5,_5#4 AS time#9]
   :  +- LogicalRDD [_1#0,_2#1,_3#2,_4#3,_5#4], MapPartitionsRDD[1] at rddToDataFrameHolder at <console>:27
   +- Project [_1#0 AS name#174,_5#4 AS time#178]
      +- Filter (_5#4 > 8)
         +- LogicalRDD [_1#0,_2#1,_3#2,_4#3,_5#4], MapPartitionsRDD[1] at rddToDataFrameHolder at <console>:27
```
可见经过优化后，逻辑计划会先对t1和t2表执行filter操作，再做Join

**例三：带sub query的LeftOuter Join**

sql:

```sql
select * from (select t1.name as name, t2.time as time from t1 inner join t2 on t1.time=t2.time) tmp_t where name is not null and time > 8
```
优化过程：

```sql
scala> sqlContext.sql("select * from (select t1.name as name, t2.time as time from t1 inner join t2 on t1.time=t2.time) tmp_t where name is not null and time > 8")
17/07/27 18:14:14 INFO parse.ParseDriver: Parsing command: select * from (select t1.name as name, t2.time as time from t1 inner join t2 on t1.time=t2.time) tmp_t where name is not null and time > 8
17/07/27 18:14:14 INFO parse.ParseDriver: Parse Completed
res78: org.apache.spark.sql.DataFrame = [name: string, time: int]

scala> res78.queryExecution.optimizedPlan
res79: org.apache.spark.sql.catalyst.plans.logical.LogicalPlan =
Project [name#5 AS name#194,time#200 AS time#195]
+- Join Inner, Some((time#9 = time#200))
   :- Project [_1#0 AS name#5,_5#4 AS time#9]
   :  +- Filter isnotnull(_1#0)
   :     +- LogicalRDD [_1#0,_2#1,_3#2,_4#3,_5#4], MapPartitionsRDD[1] at rddToDataFrameHolder at <console>:27
   +- Project [_5#4 AS time#200]
      +- Filter (_5#4 > 8)
         +- LogicalRDD [_1#0,_2#1,_3#2,_4#3,_5#4], MapPartitionsRDD[1] at rddToDataFrameHolder at <console>:27

```
可见经过优化后，逻辑计划会把子查询外部的where筛选条件分别下推至t1和t2表，再做Join

* 源码如下：

```scala
/**
  * 对Join场景或者带子查询+筛选的Join场景，可以把筛选条件下推到Join操作之前
  * 只对Join前的小表做筛选，从而降低了Join的数据
  */
object PushPredicateThroughJoin extends Rule[LogicalPlan] with PredicateHelper {
  /**
    * 把Join表达式切分成三部分： 左表条件、右表条件和公共条件
    *
    *  | left | common | right |
    *
    */
  private def split(condition: Seq[Expression], left: LogicalPlan, right: LogicalPlan) = {
    val (leftEvaluateCondition, rest) =
      condition.partition(_.references subsetOf left.outputSet)
    val (rightEvaluateCondition, commonCondition) =
      rest.partition(_.references subsetOf right.outputSet)

    (leftEvaluateCondition, rightEvaluateCondition, commonCondition)
  }

  def apply(plan: LogicalPlan): LogicalPlan = plan transform {
    // 一、子查询Join+外部Filter的场景,如：
    // select * from (select t1.id, t2.name from t1 join t2 on t1.id=t2.id) where id > 10;
    case f@Filter(filterCondition, Join(left, right, joinType, joinCondition)) =>
      val (leftFilterConditions, rightFilterConditions, commonFilterCondition) =
        split(splitConjunctivePredicates(filterCondition), left, right)

      joinType match {
        case Inner =>
          // Inner Join场景 + Filter场景，对左表和右表都做过滤器下推，然后新的Join Condition变为共有的筛选条件+外部的Filter条件
          val newLeft = leftFilterConditions.
            reduceLeftOption(And).map(Filter(_, left)).getOrElse(left)
          val newRight = rightFilterConditions.
            reduceLeftOption(And).map(Filter(_, right)).getOrElse(right)
          // 新的筛选条件=去掉左表独有的筛选条件+ 去掉右表的筛选条件 + 外部条件
          val newJoinCond = (commonFilterCondition ++ joinCondition).reduceLeftOption(And)

          Join(newLeft, newRight, Inner, newJoinCond)
        case RightOuter => ...
        case _@(LeftOuter | LeftSemi) => ...
        case FullOuter => f 
      }

    // 二、普通Join场景，按照Join的类型(Inner,LeftSemi,RightOuter,LeftOuter,FullOuter)做过滤器下推
    case f@Join(left, right, joinType, joinCondition) =>
      val (leftJoinConditions, rightJoinConditions, commonJoinCondition) =
        split(joinCondition.map(splitConjunctivePredicates).getOrElse(Nil), left, right)

      joinType match {
        case _@(Inner | LeftSemi) =>
          // Inner Join和LeftSemi Join场景，对左表和右表都做过滤器下推
          val newLeft = leftJoinConditions.
            reduceLeftOption(And).map(Filter(_, left)).getOrElse(left)
          val newRight = rightJoinConditions.
            reduceLeftOption(And).map(Filter(_, right)).getOrElse(right)
          // 新的筛选条件去掉了左表独有的条件和右表独有的条件
          val newJoinCond = commonJoinCondition.reduceLeftOption(And)

          Join(newLeft, newRight, joinType, newJoinCond)
        case RightOuter =>
          // RightOuter Join场景，只对左表做过滤器下推
          val newLeft = leftJoinConditions.
            reduceLeftOption(And).map(Filter(_, left)).getOrElse(left)
          val newRight = right
          // 新的Join条件已经把左表独有的过滤条件去掉
          val newJoinCond = (rightJoinConditions ++ commonJoinCondition).reduceLeftOption(And)

          Join(newLeft, newRight, RightOuter, newJoinCond)
        case LeftOuter =>
          // LeftOuter Join场景，只对右表做过滤器下推
          val newLeft = left
          val newRight = rightJoinConditions.
            reduceLeftOption(And).map(Filter(_, right)).getOrElse(right)
          // 新的Join条件已经把右表独有的过滤条件去掉
          val newJoinCond = (leftJoinConditions ++ commonJoinCondition).reduceLeftOption(And)

          Join(newLeft, newRight, LeftOuter, newJoinCond)
        // FullOuter Join场景，不做任何处理
        case FullOuter => f
      }
  }
}
```
