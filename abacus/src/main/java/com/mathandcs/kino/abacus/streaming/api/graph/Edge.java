package com.mathandcs.kino.abacus.streaming.api.graph;

import lombok.Data;

@Data
public class Edge {
    private String id;
    private Node   source;
    private Node   target;

    public Edge(Node source, Node target) {
        this.source = source;
        this.target = target;
        this.id = source + "->" + target;
    }

    public int getSourceId() {
        return source.getId();
    }

    public int getTargetId() {
        return target.getId();
    }
}