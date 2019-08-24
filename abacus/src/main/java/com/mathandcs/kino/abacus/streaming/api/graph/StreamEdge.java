package com.mathandcs.kino.abacus.streaming.api.graph;

import com.google.common.base.MoreObjects;
import com.mathandcs.kino.abacus.streaming.runtime.io.partition.StreamPartitioner;
import com.mathandcs.kino.abacus.streaming.api.common.AbstractID;

import java.io.Serializable;

public class StreamEdge implements Serializable {

    private String id;
    private StreamNode source;
    private StreamNode target;
    private StreamPartitioner<?> outputPartitioner;

    public StreamEdge(StreamNode source, StreamNode target, StreamPartitioner partitioner) {
        this.source = source;
        this.target = target;
        this.id = source.getId().toString() + "->" + target.getId().toString();
        this.outputPartitioner = partitioner;
    }

    public AbstractID getSourceId() {
        return source.getId();
    }

    public AbstractID getTargetId() {
        return target.getId();
    }

    public String getId() {
        return id;
    }

    public StreamNode getSource() {
        return source;
    }

    public StreamNode getTarget() {
        return target;
    }

    public StreamPartitioner<?> getOutputPartitioner() {
        return outputPartitioner;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("outputPartitioner", outputPartitioner)
                .toString();
    }
}
