package com.mathandcs.kino.abacus.streaming.api.environment.cluster;

import com.mathandcs.kino.abacus.streaming.api.common.ExecutionConfig;
import com.mathandcs.kino.abacus.streaming.api.common.JobExecutionResult;
import com.mathandcs.kino.abacus.streaming.api.environment.ExecutionEnvironment;
import com.mathandcs.kino.abacus.streaming.api.graph.StreamGraph;

public class ClusterExecutionEnvironment extends ExecutionEnvironment {

    public ClusterExecutionEnvironment(ExecutionConfig config) {
        super(config);
    }

    @Override
    public JobExecutionResult execute(StreamGraph streamGraph) throws Exception {
        // TODO: @dash
        return null;
    }
}
