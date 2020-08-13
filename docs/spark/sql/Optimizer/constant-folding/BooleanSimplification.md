### 简化布尔表达式(Boolean Simplification)


* 简化布尔表达式, 如果布尔表达式是通过逻辑门(and、or、not)等连接起来的，则根据逻辑门的特性做优化(如 ```true && a > 1``` 可以优化为 ```a > 1```, ```true || a > 1```可以优化为```true```)

例如sql:

```sql
select name from t1 where 2 > 1 and (a > 1 || 1 > 0) from t1
```
优化过程：

```sql
scala> sqlContext.sql("select name from t1 where 2 > 1 and time > 1")
17/07/26 12:10:17 INFO parse.ParseDriver: Parsing command: select name from t1 where 2 > 1 and time > 1
17/07/26 12:10:17 INFO parse.ParseDriver: Parse Completed
res26: org.apache.spark.sql.DataFrame = [name: string]

scala> res26.queryExecution
res28: org.apache.spark.sql.execution.QueryExecution =
== Parsed Logical Plan ==
'Project [unresolvedalias('name)]
+- 'Filter ((2 > 1) && ('time > 1))
   +- 'UnresolvedRelation `t1`, None

== Analyzed Logical Plan ==
name: string
Project [name#5]
+- Filter ((2 > 1) && (time#9 > 1))
   +- Subquery t1
      +- Project [_1#0 AS name#5,_2#1 AS date#6,_3#2 AS cate#7,_4#3 AS amountSpent#8,_5#4 AS time#9]
         +- LogicalRDD [_1#0,_2#1,_3#2,_4#3,_5#4], MapPartitionsRDD[1] at rddToDataFrameHolder at <console>:27

== Optimized Logical Plan ==
Project [_1#0 AS name#5]
// 2 > 1 恒为true, 此筛选条件在&&情况下
+- Filter (_5#4 > 1)
   +- LogicalRDD [_1#0,_2#1,_3#2,_4#3,_5#4], MapPartitionsRDD[1] at rddToDataFrameHolder at <console>:27
```
可见```2 > 1```这个恒为true的布尔表达式，在and操作符情况下被优化去掉了。

* 源码如下：

```scala
/**
  * Simplifies boolean expressions:
  * 1. Simplifies expressions whose answer can be determined without evaluating both sides.
  * 2. Eliminates / extracts common factors.
  * 3. Merge same expressions
  * 4. Removes `Not` operator.
  */
object BooleanSimplification extends Rule[LogicalPlan] with PredicateHelper {
  def apply(plan: LogicalPlan): LogicalPlan = plan transform {
    case q: LogicalPlan => q transformExpressionsUp {
      // and操作符的优化,如果有true的过滤器，在and条件下可以消除，如果有false，直接返回false
      case and@And(left, right) => (left, right) match {
        // true && r  =>  r
        case (Literal(true, BooleanType), r) => r
        // l && true  =>  l
        case (l, Literal(true, BooleanType)) => l
        // false && r  =>  false
        case (Literal(false, BooleanType), _) => Literal(false)
        // l && false  =>  false
        case (_, Literal(false, BooleanType)) => Literal(false)
        // a && a  =>  a
        case (l, r) if l fastEquals r => l
        // a && (not(a) || b) => a && b
        case (l, Or(l1, r)) if (Not(l) == l1) => And(l, r)
        case (l, Or(r, l1)) if (Not(l) == l1) => And(l, r)
        case (Or(l, l1), r) if (l1 == Not(r)) => And(l, r)
        case (Or(l1, l), r) if (l1 == Not(r)) => And(l, r)
        // (a || b) && (a || c)  =>  a || (b && c)
        case ...
      } // end of And(left, right)

      // or操作符的优化，短路原则
      case or@Or(left, right) => (left, right) match {
        // true || r  =>  true, 有一个为true就返回true
        case (Literal(true, BooleanType), _) => Literal(true)
        // r || true  =>  true
        case (_, Literal(true, BooleanType)) => Literal(true)
        // false || r  =>  r
        case (Literal(false, BooleanType), r) => r
        // l || false  =>  l
        case (l, Literal(false, BooleanType)) => l
        // a || a => a
        case (l, r) if l fastEquals r => l
        // (a && b) || (a && c)  =>  a && (b || c)
        case ...
      } // end of Or(left, right)
      
	   // 消除Not操作符, 直接取反义
      case not@Not(exp) => exp match {
        // not(true)  =>  false， true的反义是false
        case Literal(true, BooleanType) => Literal(false)
        // not(false)  =>  true
        case Literal(false, BooleanType) => Literal(true)
        // not(l > r)  =>  l <= r
        case GreaterThan(l, r) => LessThanOrEqual(l, r)
        // not(l >= r)  =>  l < r
        case GreaterThanOrEqual(l, r) => LessThan(l, r)
        // not(l < r)  =>  l >= r
        case LessThan(l, r) => GreaterThanOrEqual(l, r)
        // not(l <= r)  =>  l > r
        case LessThanOrEqual(l, r) => GreaterThan(l, r)
        // not(l || r) => not(l) && not(r)
        case Or(l, r) => And(Not(l), Not(r))
        // not(l && r) => not(l) or not(r)
        case And(l, r) => Or(Not(l), Not(r))
        // not(not(e))  =>  e
        case Not(e) => e
        case _ => not
      } // end of Not(exp)

      // if (true) a else b  =>  a
      // if (false) a else b  =>  b
      case e@If(Literal(v, _), trueValue, falseValue) => if (v == true) trueValue else falseValue
    }
  }
}

```
