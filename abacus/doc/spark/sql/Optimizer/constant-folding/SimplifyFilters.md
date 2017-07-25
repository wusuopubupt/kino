### 简化过滤器(Simplify Filters)

#### 
* 如果过滤器一直返回true， 则删掉此过滤器(如：where 2>1)
* 如果过滤器一直返回false, 则直接让计划返回空(如： where 2<1)

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