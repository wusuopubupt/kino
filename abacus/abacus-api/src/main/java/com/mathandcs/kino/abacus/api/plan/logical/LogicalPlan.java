package com.mathandcs.kino.abacus.api.plan.logical;

import com.mathandcs.kino.abacus.api.datastream.DataStreamId;
import com.mathandcs.kino.abacus.api.plan.Plan;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class representing the logical topology. It contains all the information
 * necessary to build the physical plan for the execution.
 */
public class LogicalPlan implements Plan {

    private static final Logger LOG = LoggerFactory.getLogger(LogicalPlan.class);

    private Map<DataStreamId, LogicalNode> logicalNodes = new HashMap<>();

    public LogicalPlan() {

    }

    public void addNode(LogicalNode node) {
        if (logicalNodes.containsKey(node.getId())) {
            LOG.warn("Node {} already in plan!", node.getId());
            return;
        }
        logicalNodes.put(node.getId(), node);
    }

    public Map<DataStreamId, LogicalNode> getLogicalNodes() {
        return logicalNodes;
    }

    @Override
    public String toDigraph() {
        StringBuilder digraph = new StringBuilder();
        digraph.append("digraph G { \n");

        logicalNodes.values().stream().forEach(
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
