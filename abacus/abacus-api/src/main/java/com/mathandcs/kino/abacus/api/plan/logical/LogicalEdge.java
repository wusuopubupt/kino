package com.mathandcs.kino.abacus.api.plan.logical;

import akka.actor.ActorRef;
import com.mathandcs.kino.abacus.api.partition.Partitioner;
import java.io.Serializable;

public class LogicalEdge implements Serializable {

    private final String id;
    private final LogicalNode source;
    private final LogicalNode target;
    private final Partitioner partitioner;

    private ActorRef actor;

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
