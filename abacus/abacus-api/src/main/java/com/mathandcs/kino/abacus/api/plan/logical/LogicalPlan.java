package com.mathandcs.kino.abacus.api.plan.logical;

import com.mathandcs.kino.abacus.api.datastream.DataStreamId;
import com.mathandcs.kino.abacus.api.plan.Plan;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class representing the logical topology. It contains all the information
 * necessary to build the physical plan for the execution.
 */
public class LogicalPlan implements Plan {

    private static final Logger LOG = LoggerFactory.getLogger(LogicalPlan.class);

    private Map<DataStreamId, LogicalNode> idToNodeMap = new HashMap<>();

    public LogicalPlan() {

    }

    public void addNode(LogicalNode node) {
        idToNodeMap.put(node.getId(), node);
    }

    public LogicalNode getNode(DataStreamId id) {
        return idToNodeMap.get(id);
    }

    public Map<DataStreamId, LogicalNode> getIdToNodeMap() {
        return idToNodeMap;
    }

    public List<LogicalNode> getAllNodes() {
        return idToNodeMap.values().stream().collect(Collectors.toList());
    }

    @Override
    public String toDigraph() {
        StringBuilder digraph = new StringBuilder();
        digraph.append("digraph G { \n");

        idToNodeMap.values().stream().forEach(
            node -> {
                List<LogicalEdge> edges = new ArrayList<>();
                edges.addAll(node.getInputEdges());
                edges.addAll(node.getOutputEdges());
                edges.forEach(edge -> digraph.append(edge.getSource().getId()).append(" -> ")
                    .append(edge.getTarget().getId()).append(";\n"));
            }
        );

        digraph.append("}");
        return digraph.toString();
    }

}
