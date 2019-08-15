package com.mathandcs.kino.abacus.streaming.api.optimizer;

import com.mathandcs.kino.abacus.streaming.api.common.JobID;
import com.mathandcs.kino.abacus.streaming.runtime.jobgraph.JobGraph;

public abstract class StreamPlan implements KinoPlan {

    public abstract JobGraph getJobGraph(JobID jobID);

    public abstract String toJson();

}
