package com.mathandcs.kino.abacus.streaming.api.graph;

import com.mathandcs.kino.abacus.streaming.api.environment.Environment;
import com.mathandcs.kino.abacus.streaming.api.operators.Operator;
import java.util.Map;
import java.util.Set;
import lombok.Data;

@Data
public class Graph {

    private Environment        env;
    private Map<Integer, Node> allNodes;
    private Set<Integer>       sourceNodeIds;
    private Set<Integer>       sinkNodeIds;

    public Graph(Environment env) {
        this.env = env;
    }

    public <IN, OUT> void addSource(Integer vertexId) {

    }

    private Node addNode(Integer nodeId, String operatorName, Operator<?> operator) {
        Node node = new Node(env, nodeId, operatorName, operator);
        allNodes.put(nodeId, node);
        return node;
    }
}