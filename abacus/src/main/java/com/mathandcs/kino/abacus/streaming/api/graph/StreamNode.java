package com.mathandcs.kino.abacus.streaming.api.graph;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.mathandcs.kino.abacus.streaming.api.functions.KeySelector;
import com.mathandcs.kino.abacus.streaming.api.graph.tasks.AbstractInvokable;
import com.mathandcs.kino.abacus.streaming.api.operators.Operator;
import com.mathandcs.kino.abacus.streaming.runtime.utils.AbstractID;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    public AbstractID getId() {
        return id;
    }

    public Operator getOperator() {
        return operator;
    }

    public int getParallelism() {
        return parallelism;
    }

    public int getMaxParallelism() {
        return maxParallelism;
    }

    public KeySelector<?, ?> getStatePartitioner1() {
        return statePartitioner1;
    }

    public KeySelector<?, ?> getStatePartitioner2() {
        return statePartitioner2;
    }

    public List<StreamEdge> getInEdges() {
        return inEdges;
    }

    public List<StreamEdge> getOutEdges() {
        return outEdges;
    }

    public Class<? extends AbstractInvokable> getJobVertexClass() {
        return jobVertexClass;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id.toString())
                .add("operator", operator)
                .add("parallelism", parallelism)
                .add("inEdges", inEdges)
                .add("outEdges", outEdges)
                .add("jobVertexClass", jobVertexClass)
                .toString();
    }
}
