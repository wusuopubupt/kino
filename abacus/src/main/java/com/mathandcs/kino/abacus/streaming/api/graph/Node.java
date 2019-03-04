package com.mathandcs.kino.abacus.streaming.api.graph;

import com.mathandcs.kino.abacus.streaming.api.environment.Environment;
import com.mathandcs.kino.abacus.streaming.api.operators.Operator;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class Node implements Serializable {

    private Environment env;
    private int         id;
    private String      operatorName;
    private Operator<?> operator;
    private List<Edge>  inEdges  = new ArrayList<>();
    private List<Edge>  outEdges = new ArrayList<>();

    private int parallelism;
    private int maxParallelism;

    public Node(Environment env, int id, String operatorName, Operator<?> operator) {
        this.env = env;
        this.id = id;
        this.operatorName = operatorName;
        this.operator = operator;
    }

    public void addInEdge(Edge inEdge) {
        if (inEdge.getTargetId() != this.id) {
            throw new IllegalArgumentException("Target id doesn't match current Node id.");
        }
        inEdges.add(inEdge);
    }

    public void addOutEdge(Edge outEdge) {
        if (outEdge.getSourceId() != this.id) {
            throw new IllegalArgumentException("Source id doesn't match current Node id.");
        }
        outEdges.add(outEdge);
    }

}