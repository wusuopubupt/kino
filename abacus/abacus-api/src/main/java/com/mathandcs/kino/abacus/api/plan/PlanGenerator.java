package com.mathandcs.kino.abacus.api.plan;

public interface PlanGenerator<PLAN> {

  /**
   * @return generated plan.
   */
  PLAN generate();

}
