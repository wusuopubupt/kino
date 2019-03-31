package com.mathandcs.kino.abacus.streaming.runtime.executiongraph;

import com.mathandcs.kino.abacus.streaming.runtime.jobgraph.JobGraph;
import com.mathandcs.kino.abacus.streaming.runtime.jobgraph.JobVertex;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExecutionGraphBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(ExecutionGraphBuilder.class);

    public ExecutionGraph build(JobGraph jobGraph) {
        ExecutionGraph executionGraph = new ExecutionGraph();

        JobInformation jobInformation = new JobInformation(jobGraph.getJobName());
        executionGraph.setJobInformation(jobInformation);

        // topologically sort the job vertices and attach the graph to the existing one
        List<JobVertex> sortedTopology = jobGraph.getVerticesSortedTopologicallyFromSources();
        LOG.info("Adding {} vertices from job graph {}.", sortedTopology.size(), jobGraph.getJobName());
        attachJobGraph(executionGraph, sortedTopology);
        LOG.info("Successfully created execution graph from job graph {}.", jobGraph.getJobName());
        return executionGraph;
    }

    private void attachJobGraph(ExecutionGraph executionGraph, List<JobVertex> sortedTopology) {
        for (JobVertex jobVertex : sortedTopology) {
            ExecutionJobVertex executionJobVertex = new ExecutionJobVertex();
            executionJobVertex.setGraph(executionGraph);
            executionJobVertex.setJobVertex(jobVertex);
            // TODO
        }
    }
}
