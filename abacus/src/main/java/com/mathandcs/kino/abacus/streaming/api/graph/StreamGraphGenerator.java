package com.mathandcs.kino.abacus.streaming.api.graph;

import com.mathandcs.kino.abacus.streaming.api.common.ExecutionConfig;
import com.mathandcs.kino.abacus.streaming.api.datastream.DataStreamSink;
import com.mathandcs.kino.abacus.streaming.api.datastream.DataStreamSource;
import com.mathandcs.kino.abacus.streaming.api.datastream.OneInputDataStream;
import com.mathandcs.kino.abacus.streaming.api.datastream.Transformable;
import com.mathandcs.kino.abacus.streaming.api.common.AbstractID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StreamGraphGenerator {

    private static final Logger LOG = LoggerFactory.getLogger(StreamGraphGenerator.class);

    private final List<Transformable> transformables;
    private final ExecutionConfig executionConfig;
    private Map<Transformable, Collection<AbstractID>> alreadyTransformed;
    private StreamGraph streamGraph;

    public StreamGraphGenerator(List<Transformable> transformables, ExecutionConfig executionConfig) {
        this.transformables = transformables;
        this.executionConfig = executionConfig;
        this.alreadyTransformed = new HashMap<>();
    }

    public StreamGraph generate() {
        streamGraph = new StreamGraph(executionConfig);

        LOG.info("Transforables: {}.", transformables);
        for (Transformable transformable : transformables) {
            alreadyTransformed.put(transformable, transform(transformable));
        }

        final StreamGraph builtStreamGraph = streamGraph;
        LOG.info("Generated stream graph: {}.", builtStreamGraph.toJson());

        // clear
        alreadyTransformed.clear();
        alreadyTransformed = null;
        streamGraph = null;

        return builtStreamGraph;
    }

    private Collection<AbstractID> transform(Transformable transformable) {
        if (alreadyTransformed.containsKey(transformable)) {
            return alreadyTransformed.get(transformable);
        }

        LOG.info("Transforming " + transformable.toString());

        Collection<AbstractID> transformedIds;
        if (transformable instanceof OneInputDataStream<?, ?>) {
            // one input operator
            transformedIds = transformOneInputTransformable((OneInputDataStream<?, ?>) transformable);
        } else if (transformable instanceof DataStreamSource<?>) {
            // source operator
            transformedIds = transformSource((DataStreamSource<?>) transformable);
        } else if (transformable instanceof DataStreamSink<?>) {
            // sink operator
            transformedIds = transformSink((DataStreamSink<?>) transformable);
        } else {
            throw new IllegalStateException("Unknown transformation: " + transformable);
        }

        return transformedIds;
    }

    private Collection<AbstractID> transformSource(DataStreamSource source) {
        streamGraph.addSource(source.getId(), source.getOperator());
        return Collections.singleton(source.getId());
    }

    private Collection<AbstractID> transformSink(DataStreamSink sink) {
        Collection<AbstractID> inputIds = transform(sink.getInput());

        streamGraph.addSink(sink.getId(), sink.getOperator());

        for (AbstractID inputId: inputIds) {
            streamGraph.addEdge(inputId, sink.getId(), null);
        }

        return Collections.emptyList();
    }

    private Collection<AbstractID> transformOneInputTransformable(OneInputDataStream transformable) {
        Collection<AbstractID> inputIds = transform(transformable.getInput());

        // the recursive call might have already transformed this
        if (alreadyTransformed.containsKey(transformable)) {
            return alreadyTransformed.get(transformable);
        }

        streamGraph.addOperator(transformable.getId(),transformable.getOperator());

        for (AbstractID inputId: inputIds) {
            streamGraph.addEdge(inputId, transformable.getId(), null);
        }

        return Collections.singleton(transformable.getId());
    }

}
