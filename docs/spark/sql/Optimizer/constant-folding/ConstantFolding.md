### 常量合并(Constant Folding)


* 替换可以被静态计算的表达式

例如sql:

```sql
select 1+2+3 from t1
```
优化过程：

```sql
scala> sqlContext.sql("select 1+2+3 from t1")
17/07/25 16:50:21 INFO parse.ParseDriver: Parsing command: select 1+2+3 from t1
17/07/25 16:50:21 INFO parse.ParseDriver: Parse Completed
res27: org.apache.spark.sql.DataFrame = [_c0: int]

scala> res27.queryExecution
res28: org.apache.spark.sql.execution.QueryExecution =
== Parsed Logical Plan ==
'Project [unresolvedalias(((1 + 2) + 3))]
+- 'UnresolvedRelation `t1`, None

== Analyzed Logical Plan ==
_c0: int
Project [((1 + 2) + 3) AS _c0#19]
+- Subquery t1
   +- Project [_1#0 AS name#5,_2#1 AS date#6,_3#2 AS cate#7,_4#3 AS amountSpent#8,_5#4 AS time#9]
      +- LogicalRDD [_1#0,_2#1,_3#2,_4#3,_5#4], MapPartitionsRDD[1] at rddToDataFrameHolder at <console>:27

== Optimized Logical Plan ==
Project [6 AS _c0#19]
+- LogicalRDD [_1#0,_2#1,_3#2,_4#3,_5#4], MapPartitionsRDD[1] at rddToDataFrameHolder at <console>:27

== Physical Plan ==
Project [6 AS _c0#19]
+- Scan ExistingRDD[_1#0,_2#1,_3#2,_4#3,_5#4]
```
可见经过优化后，逻辑计划里的project转化成了6（1+2+3的结果），物理计划直接返回6

* 源码如下：

```scala
/**
  * 替换可以被静态计算的表达式
  */
object ConstantFolding extends Rule[LogicalPlan] {
  def apply(plan: LogicalPlan): LogicalPlan = plan transform {
    case q: LogicalPlan => q transformExpressionsDown { // 对计划的表达式执行转化操作
      // 如果是字面量，直接返回，避免对字面量的重复计算(因为Literal也是foldable的)
      case l: Literal => l
      // 调用eval方法合并foldable的表达式,返回字面量
      case e if e.foldable => Literal.create(e.eval(EmptyRow), e.dataType)
    }
  }
}

```
