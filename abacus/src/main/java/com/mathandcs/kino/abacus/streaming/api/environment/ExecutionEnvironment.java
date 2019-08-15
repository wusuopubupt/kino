package com.mathandcs.kino.abacus.streaming.api.environment;

import com.google.common.base.Preconditions;
import com.mathandcs.kino.abacus.streaming.api.common.ExecutionConfig;
import com.mathandcs.kino.abacus.streaming.api.common.JobExecutionResult;
import com.mathandcs.kino.abacus.streaming.api.datastream.DataStreamSource;
import com.mathandcs.kino.abacus.streaming.api.functions.SourceFunction;
import com.mathandcs.kino.abacus.streaming.api.functions.source.FromElementsFunction;
import com.mathandcs.kino.abacus.streaming.api.graph.StreamGraph;
import com.mathandcs.kino.abacus.streaming.api.operators.SourceOperator;
import com.mathandcs.kino.abacus.streaming.runtime.state.StateBackend;

import java.io.IOException;
import java.util.Collection;

public abstract class ExecutionEnvironment implements Environment{

    /** The default name to use for a streaming job if no other name has been specified. */
    public static final String DEFAULT_JOB_NAME = "Kino Streaming Job";

    /** The execution configuration for this environment. */
    private final ExecutionConfig config = new ExecutionConfig();

    protected boolean isChainingEnabled = true;

    /** The state backend used for storing k/v state and state snapshots. */
    private StateBackend stateBackend;

    // --------------------------------------------------------------------------------------------
    // Constructor and Properties
    // --------------------------------------------------------------------------------------------

    public ExecutionConfig getConfig() {
        return config;
    }

    public abstract JobExecutionResult execute(StreamGraph streamGraph) throws Exception;

    public JobExecutionResult execute(String jobName) throws Exception {
        return execute(getStreamGraph(jobName));
    }

    public StreamGraph getStreamGraph(String jobName) {
        return getStreamGraphGenerator().setJobName(jobName).generate();
    }

    // --------------------------------------------------------------------------------------------
    // Data stream creations
    // --------------------------------------------------------------------------------------------

    /**
     * Creates a data stream from the given non-empty collection and parallelism
     */
    public <OUT> DataStreamSource<OUT> fromCollection(Collection<OUT> data, int parallelism) {
        Preconditions.checkNotNull(data, "Collection must not be null");

        SourceFunction<OUT> function;
        try {
            function = new FromElementsFunction(data);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        SourceOperator sourceOperator = new SourceOperator(function);
        return new DataStreamSource<>(this, null, sourceOperator);
    }

}
