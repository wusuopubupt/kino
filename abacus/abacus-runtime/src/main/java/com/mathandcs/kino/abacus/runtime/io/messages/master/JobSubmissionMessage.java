package com.mathandcs.kino.abacus.runtime.io.messages.master;

import com.mathandcs.kino.abacus.api.plan.logical.LogicalPlan;
import com.mathandcs.kino.abacus.runtime.io.messages.master.MasterMessage;

public class JobSubmissionMessage implements MasterMessage {

  private LogicalPlan logicalPlan;

  public JobSubmissionMessage(LogicalPlan logicalPlan) {
    this.logicalPlan = logicalPlan;
  }

  public LogicalPlan getLogicalPlan() {
    return logicalPlan;
  }

}
