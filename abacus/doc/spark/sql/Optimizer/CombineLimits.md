### 合并limit语句(Combine Limits)

合并相邻的限制条件，例如：
```select id from (select * from t1 limit 100) tmp_t limit 10```

外层的limit 10小于子查询的limit 100，所以子查询内部的limit 100会被优化为limit 10

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