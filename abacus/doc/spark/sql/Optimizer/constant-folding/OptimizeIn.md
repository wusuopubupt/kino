### 优化In语句(Optimize In)


* 把 v In(1,1,2,2,1,2,1,2,2,2,2,2)优化为InSet(v, [1,2])  （**存疑**）

例如sql:

```sql
select * from t1 where id in (1,1,2,2,1,2,1,2,2,2,2,2)
```
**注意：** 我的测试环境spark-1.6.2没有看出优化！

* 源码如下：

```scala
/**
  * Replaces [[In (value, seq[Literal])]] with optimized version[[InSet (value, HashSet[Literal])]]
  * which is much faster
  */
object OptimizeIn extends Rule[LogicalPlan] {
  def apply(plan: LogicalPlan): LogicalPlan = plan transform {
    case q: LogicalPlan => q transformExpressionsDown {
      case In(v, list) if !list.exists(!_.isInstanceOf[Literal]) && list.size > 10 =>
        val hSet = list.map(e => e.eval(EmptyRow))
        InSet(v, HashSet() ++ hSet)
    }
  }
}

```
