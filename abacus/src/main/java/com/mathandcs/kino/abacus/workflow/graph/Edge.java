package com.mathandcs.kino.abacus.workflow.graph;

import lombok.Data;

/**
 * Created by dashwang on 6/14/17.
 */
@Data
public class Edge {

    private Node sourceNode;
    private Node targetNode;

    public Edge(Node sourceNode, Node targetNode) {
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
    }
}
