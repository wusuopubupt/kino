package com.mathandcs.kino.abacus.streaming.api.environment.local;

import com.mathandcs.kino.abacus.streaming.api.common.AbstractID;
import com.mathandcs.kino.abacus.streaming.api.common.ExecutionConfig;
import com.mathandcs.kino.abacus.streaming.api.common.JobExecutionResult;
import com.mathandcs.kino.abacus.streaming.api.common.JobID;
import com.mathandcs.kino.abacus.streaming.api.environment.ExecutionEnvironment;
import com.mathandcs.kino.abacus.streaming.api.graph.StreamGraph;
import com.mathandcs.kino.abacus.streaming.api.graph.StreamNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

public class LocalExecutionEnvironment extends ExecutionEnvironment {

    private static final Logger LOG = LoggerFactory.getLogger(LocalExecutionEnvironment.class);

    public LocalExecutionEnvironment(ExecutionConfig config) {
        super(config);
    }

    @Override
    public JobExecutionResult execute(StreamGraph streamGraph) throws Exception {
        Set<AbstractID> sources = streamGraph.getSources();
        Map<AbstractID, StreamNode> nodes = streamGraph.getStreamNodes();
        for (AbstractID source : sources) {
            StreamNode sourceNode = nodes.get(source);
            LOG.info(sourceNode.getOperator().getName());
        }

        return new JobExecutionResult(new JobID());
    }

}
