package com.mathandcs.kino.abacus.api.plan;

import java.io.Serializable;

public interface Plan extends Serializable {

  /**
   * Generate digraph by this plan.
   */
  String toDigraph();

}
