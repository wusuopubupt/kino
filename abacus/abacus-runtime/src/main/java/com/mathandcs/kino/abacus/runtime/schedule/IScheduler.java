package com.mathandcs.kino.abacus.runtime.schedule;

import com.mathandcs.kino.abacus.api.plan.logical.LogicalPlan;

public interface IScheduler {

    void schedule(LogicalPlan plan);

}
