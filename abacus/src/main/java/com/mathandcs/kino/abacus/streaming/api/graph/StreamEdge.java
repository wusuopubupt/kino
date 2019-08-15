package com.mathandcs.kino.abacus.streaming.api.graph;

import com.mathandcs.kino.abacus.streaming.runtime.io.partition.StreamPartitioner;
import com.mathandcs.kino.abacus.streaming.runtime.utils.AbstractID;
import lombok.Data;

import java.io.Serializable;

@Data
public class StreamEdge implements Serializable {

    private String id;
    private StreamNode source;
    private StreamNode target;
    private StreamPartitioner<?> outputPartitioner;

    public StreamEdge(StreamNode source, StreamNode target, StreamPartitioner partitioner) {
        this.source = source;
        this.target = target;
        this.id = source.getId() + "_" + target.getId();
        this.outputPartitioner = partitioner;
    }

    public AbstractID getSourceId() {
        return source.getId();
    }

    public AbstractID getTargetId() {
        return target.getId();
    }

}
