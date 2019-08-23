package com.mathandcs.kino.abacus.streaming.api.environment;

import com.google.common.base.Preconditions;
import com.mathandcs.kino.abacus.streaming.api.common.ExecutionConfig;
import com.mathandcs.kino.abacus.streaming.api.common.JobExecutionResult;
import com.mathandcs.kino.abacus.streaming.api.common.RunMode;
import com.mathandcs.kino.abacus.streaming.api.datastream.DataStreamSource;
import com.mathandcs.kino.abacus.streaming.api.datastream.Transformable;
import com.mathandcs.kino.abacus.streaming.api.environment.cluster.ClusterExecutionEnvironment;
import com.mathandcs.kino.abacus.streaming.api.environment.local.LocalExecutionEnvironment;
import com.mathandcs.kino.abacus.streaming.api.functions.SourceFunction;
import com.mathandcs.kino.abacus.streaming.api.functions.source.FromElementsFunction;
import com.mathandcs.kino.abacus.streaming.api.graph.StreamGraph;
import com.mathandcs.kino.abacus.streaming.api.graph.StreamGraphGenerator;
import com.mathandcs.kino.abacus.streaming.api.operators.SourceOperator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class ExecutionEnvironment implements Environment{

    /** The execution configuration for this environment. */
    private final ExecutionConfig config;

    /** The transformable list */
    private List<Transformable> transformables = new ArrayList<>(16);

    // --------------------------------------------------------------------------------------------
    // Constructor and Properties
    // --------------------------------------------------------------------------------------------

    public ExecutionEnvironment(ExecutionConfig config) {
        this.config = config;
    }

    public ExecutionConfig getConfig() {
        return config;
    }

    public void addTransformable(Transformable transformable) {
        Preconditions.checkNotNull(transformable, "Transformable can not be null!");
        transformables.add(transformable);
    }

    public static ExecutionEnvironment getExecutionEnvironment(ExecutionConfig config) {
        if (config.getRunMode() == RunMode.LOCAL) {
            return new LocalExecutionEnvironment(config);
        } else {
            return new ClusterExecutionEnvironment(config);
        }
    }

    // --------------------------------------------------------------------------------------------
    // Stream graph generation and execution
    // --------------------------------------------------------------------------------------------

    public abstract JobExecutionResult execute(StreamGraph streamGraph) throws Exception;

    public JobExecutionResult execute(String jobName) throws Exception {
        config.setJobName(jobName);
        return execute(getStreamGraph());
    }

    public StreamGraph getStreamGraph() {
        return getStreamGraphGenerator().generate();
    }

    private StreamGraphGenerator getStreamGraphGenerator() {
        return new StreamGraphGenerator(transformables, config);
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
        DataStreamSource streamSource = new DataStreamSource<>(this, null, sourceOperator);
        transformables.add(streamSource);
        return streamSource;
    }

}
