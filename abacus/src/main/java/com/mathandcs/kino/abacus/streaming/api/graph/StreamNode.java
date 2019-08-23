package com.mathandcs.kino.abacus.streaming.api.graph;

import com.google.common.base.Preconditions;
import com.mathandcs.kino.abacus.streaming.api.functions.KeySelector;
import com.mathandcs.kino.abacus.streaming.api.graph.tasks.AbstractInvokable;
import com.mathandcs.kino.abacus.streaming.api.operators.Operator;
import com.mathandcs.kino.abacus.streaming.runtime.utils.AbstractID;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class StreamNode implements Serializable {

    private static final long serialVersionID = 1L;

    private final AbstractID id;
    private final Operator operator;
    private int parallelism;
    private int maxParallelism;
    private KeySelector<?, ?> statePartitioner1;
    private KeySelector<?, ?> statePartitioner2;

    private List<StreamEdge> inEdges = new ArrayList();
    private List<StreamEdge> outEdges = new ArrayList();

    private final Class<? extends AbstractInvokable> jobVertexClass;

    public StreamNode(
            AbstractID id,
            Operator operator,
            Class<? extends AbstractInvokable> jobVertexClass) {

        this.id = id;
        this.operator = operator;
        this.jobVertexClass = jobVertexClass;
    }

    public void addInEdge(StreamEdge inEdge) {
        Preconditions.checkState(inEdge.getTargetId() == getId(),
                "Destination id doesn't match the StreamNode id.");
        inEdges.add(inEdge);
    }

    public void addOutEdge(StreamEdge outEdge) {
        Preconditions.checkState(outEdge.getSourceId() == getId(),
                "Source id doesn't match the StreamNode id.");
        outEdges.add(outEdge);
    }
}
