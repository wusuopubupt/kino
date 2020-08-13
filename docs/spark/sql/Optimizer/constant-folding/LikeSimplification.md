### 简化Like语句(Like Simplification)


对一下几种场景:

-  startsWith:  'abc%'
-  endsWith: '%abc'
-  contains: '%abc%'
-  equalTo: 'abc'

做了优化

例如sql:

```sql
select name from t1 where name like 'Bo%'
```
优化过程：

```sql
scala> sqlContext.sql("select name from t1 where name like 'B%'")
17/07/25 18:25:04 INFO parse.ParseDriver: Parsing command: select name from t1 where name like 'B%'
17/07/25 18:25:04 INFO parse.ParseDriver: Parse Completed
res46: org.apache.spark.sql.DataFrame = [name: string]

scala> res46.queryExecution
res47: org.apache.spark.sql.execution.QueryExecution =
== Parsed Logical Plan ==
'Project [unresolvedalias('name)]
+- 'Filter 'name LIKE B%
   +- 'UnresolvedRelation `t1`, None

== Analyzed Logical Plan ==
name: string
Project [name#5]
+- Filter name#5 LIKE B%
   +- Subquery t1
      +- Project [_1#0 AS name#5,_2#1 AS date#6,_3#2 AS cate#7,_4#3 AS amountSpent#8,_5#4 AS time#9]
         +- LogicalRDD [_1#0,_2#1,_3#2,_4#3,_5#4], MapPartitionsRDD[1] at rddToDataFrameHolder at <console>:27

== Optimized Logical Plan ==
Project [_1#0 AS name#5]
+- Filter StartsWith(_1#0, B) // 优化为字符串的startWith()
   +- LogicalRDD [_1#0,_2#1,_3#2,_4#3,_5#4], MapPartitionsRDD[1] at rddToDataFrameHolder at <console>:27

== Physical Plan ==
Project [_1#0 AS name#5]
+- Filter StartsWith(_1#0, B)
   +- Scan ExistingRDD[_1#0,_2#1,_3#2,_...
```
可见经过优化后，原始输入的正则表达式转化为字符串的startWith()操作

* 源码如下：

```scala
/**
  * 简化不需要使用正则表达式匹配的like语句
  */
object LikeSimplification extends Rule[LogicalPlan] {
  // if guards below protect from escapes on trailing %.
  // Cases like "something\%" are not optimized, but this does not affect correctness.
  private val startsWith = "([^_%]+)%".r  // 'abc%'
  private val endsWith = "%([^_%]+)".r    // '%abc'
  private val contains = "%([^_%]+)%".r   // '%abc%'
  private val equalTo = "([^_%]*)".r      // 'abc'

  def apply(plan: LogicalPlan): LogicalPlan = plan transformAllExpressions {
    case Like(l, Literal(utf, StringType)) =>
      utf.toString match {
        case startsWith(pattern) if !pattern.endsWith("\\") =>
          StartsWith(l, Literal(pattern)) // 字符串的startWith()
        case endsWith(pattern) =>
          EndsWith(l, Literal(pattern))   // 字符串的endWith()
        case contains(pattern) if !pattern.endsWith("\\") =>
          Contains(l, Literal(pattern))   // 通过字节码检查包含
        case equalTo(pattern) =>
          EqualTo(l, Literal(pattern))    // 字符串的=操作
        case _ =>
          Like(l, Literal.create(utf, StringType))
      }
  }
}

```
