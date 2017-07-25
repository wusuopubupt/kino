### 删除非必要的表达式(Remove Dispensable Expressions)


* 删除非必要的表达式?(没看懂)

例如sql:

```sql

```
优化过程：

```sql

```
可见经过优化后..

* 源码如下：

```scala
/**
  * Removes nodes that are not necessary.
  */
object RemoveDispensableExpressions extends Rule[LogicalPlan] {
  def apply(plan: LogicalPlan): LogicalPlan = plan transformAllExpressions {
    case UnaryPositive(child) => child
    case PromotePrecision(child) => child
  }
}

```
