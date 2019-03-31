package com.mathandcs.kino.abacus.streaming.runtime.executiongraph;

import com.mathandcs.kino.abacus.streaming.runtime.jobgraph.JobVertex;
import com.mathandcs.kino.abacus.streaming.runtime.jobgraph.JobVertexID;
import java.util.List;
import java.util.Map;

/**
 * Interface of ExecutionGraph
 */
public interface IExecutionGraph {
    /**
     * @return job name for this execution graph
     */
    String getJobName();

    /**
     *
     * @param id if of job vertex to be returned
     * @return job vertex for the given id, or {@code null}
     */
    ExecutionJobVertex getJobVertex(JobVertexID id);

    /**
     * @return a map containing all job vertices for this execution graph
     */
    Map<JobVertexID, ExecutionJobVertex> getAllVertices();

    /**
     *
     * @return a iterable containing all job vertices for this execution graph in the order they were created
     */
    Iterable<ExecutionJobVertex> getVerticesTopologically();

}