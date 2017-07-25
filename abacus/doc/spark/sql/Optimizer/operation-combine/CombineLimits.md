### 合并limit语句(Combine Limits)

* 合并相邻的限制条件，例如：
```select name from (select name, date from t1 limit 100) tmp_t limit 10```

   外层的limit 10小于子查询的limit 100，所以子查询内部的limit 100会被优化为limit 10(见下面的Optimized Logical Plan): 
   
``` sql
scala> sqlContext.sql("select name from (select name, date from t1 limit 100) tmp_t limit 10")
17/07/25 13:54:19 INFO parse.ParseDriver: Parsing command: select name from (select name, date from t1 limit 100) tmp_t limit 10
17/07/25 13:54:19 INFO parse.ParseDriver: Parse Completed
res11: org.apache.spark.sql.DataFrame = [name: string]

scala> res11.queryExecution
res12: org.apache.spark.sql.execution.QueryExecution =
== Parsed Logical Plan ==
'Limit 10
+- 'Project [unresolvedalias('name)]
   +- 'Subquery tmp_t
      +- 'Limit 100
         +- 'Project [unresolvedalias('name),unresolvedalias('date)]
            +- 'UnresolvedRelation `t1`, None

== Analyzed Logical Plan ==
name: string
Limit 10
+- Project [name#5]
   +- Subquery tmp_t
      +- Limit 100
         +- Project [name#5,date#6]
            +- Subquery t1
               +- Project [_1#0 AS name#5,_2#1 AS date#6,_3#2 AS cate#7,_4#3 AS amountSpent#8,_5#4 AS time#9]
                  +- LogicalRDD [_1#0,_2#1,_3#2,_4#3,_5#4], MapPartitionsRDD[2] at rddToDataFrameHolder at <console>:27

== Optimized Logical Plan ==
Limit 10
+- Project [_1#0 AS name#5]
   +- LogicalRDD [_1#0,_2#1,_3#2,_4#3,_5...
```

* 源码如下：

```scala
/**
  * Combines two adjacent [[Limit]] operators into one, merging the
  * expressions into one single expression.
  */
object CombineLimits extends Rule[LogicalPlan] {
  def apply(plan: LogicalPlan): LogicalPlan = plan transform {
    case ll@Limit(le, nl@Limit(ne, grandChild)) => //ll为当前Limit,le为其expression; nl是ll的grandChild，ne是nl的expression  
      Limit(If(LessThan(ne, le), ne, le), grandChild) //expression比较，如果ne比le小则表达式为ne，否则为le  
  }
}
```