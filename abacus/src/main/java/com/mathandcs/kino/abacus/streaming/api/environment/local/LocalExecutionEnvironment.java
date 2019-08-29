package com.mathandcs.kino.abacus.streaming.api.environment.local;

import com.mathandcs.kino.abacus.streaming.api.common.ExecutionConfig;
import com.mathandcs.kino.abacus.streaming.api.common.JobExecutionResult;
import com.mathandcs.kino.abacus.streaming.api.common.JobID;
import com.mathandcs.kino.abacus.streaming.api.environment.ExecutionEnvironment;
import com.mathandcs.kino.abacus.streaming.api.graph.StreamGraph;
import com.mathandcs.kino.abacus.streaming.api.graph.StreamNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class LocalExecutionEnvironment extends ExecutionEnvironment {

    private static final Logger LOG = LoggerFactory.getLogger(LocalExecutionEnvironment.class);

    public LocalExecutionEnvironment(ExecutionConfig config) {
        super(config);
    }



    @Override
    public JobExecutionResult execute(StreamGraph streamGraph) throws Exception {
        executeStreamGraph(streamGraph);

        JobID jobID = new JobID();
        return new JobExecutionResult(jobID);
    }


    private void executeStreamGraph(StreamGraph streamGraph) {
        Set<StreamNode> sources = streamGraph.getSources();
        for (StreamNode source : sources) {
            try {
                source.getOperator().open();
            } catch (Exception e) {
                LOG.warn("Failed to open operator.", e);
            }

            source.getOperator().
        }

    }
}
