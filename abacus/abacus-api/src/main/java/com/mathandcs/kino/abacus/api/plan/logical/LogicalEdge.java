package com.mathandcs.kino.abacus.api.plan.logical;

import com.mathandcs.kino.abacus.core.io.Partitioner;
import java.io.Serializable;

public class LogicalEdge implements Serializable {

    private final String id;
    private final LogicalNode source;
    private final LogicalNode target;
    private final Partitioner partitioner;

    public LogicalEdge(LogicalNode source, LogicalNode target, Partitioner partitioner) {
        this.source = source;
        this.target = target;
        this.id = source.getId().toString() + "->" + target.getId().toString();
        this.partitioner = partitioner;
    }

    @Override
    public String toString() {
        return id;
    }

    public String getId() {
        return id;
    }

    public LogicalNode getSource() {
        return source;
    }

    public LogicalNode getTarget() {
        return target;
    }

    public Partitioner getPartitioner() {
        return partitioner;
    }
}
