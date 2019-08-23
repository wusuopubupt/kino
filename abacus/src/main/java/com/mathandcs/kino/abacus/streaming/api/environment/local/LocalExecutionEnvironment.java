package com.mathandcs.kino.abacus.streaming.api.environment.local;

import com.mathandcs.kino.abacus.streaming.api.common.ExecutionConfig;
import com.mathandcs.kino.abacus.streaming.api.common.JobExecutionResult;
import com.mathandcs.kino.abacus.streaming.api.environment.ExecutionEnvironment;
import com.mathandcs.kino.abacus.streaming.api.graph.StreamGraph;

public class LocalExecutionEnvironment extends ExecutionEnvironment {

    public LocalExecutionEnvironment(ExecutionConfig config) {
        super(config);
    }

    @Override
    public JobExecutionResult execute(StreamGraph streamGraph) throws Exception {
        // TODO: @dash
        return null;
    }

}
