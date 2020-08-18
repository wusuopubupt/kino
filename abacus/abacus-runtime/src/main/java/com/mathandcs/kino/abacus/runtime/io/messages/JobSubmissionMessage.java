package com.mathandcs.kino.abacus.runtime.io.messages;

import com.mathandcs.kino.abacus.api.plan.logical.LogicalPlan;

public class JobSubmissionMessage implements MasterMessage {

  private LogicalPlan logicalPlan;

  public JobSubmissionMessage(LogicalPlan logicalPlan) {
    this.logicalPlan = logicalPlan;
  }

  public LogicalPlan getLogicalPlan() {
    return logicalPlan;
  }

}
