package com.mathandcs.kino.abacus.streaming.runtime.jobgraph;

import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Data;

@Data
public class JobGraph {
    private final String                      jobName;
    private       Map<JobVertexID, JobVertex> jobVertices = new LinkedHashMap<>();

    public JobGraph(String jobName, JobVertex... jobVertices) {
        this.jobName = jobName;
        for (JobVertex jobVertex : jobVertices) {
            addVertex(jobVertex);
        }
    }

    public void addVertex(JobVertex jobVertex) {
        JobVertexID id = jobVertex.getId();
        JobVertex previous = jobVertices.put(id, jobVertex);
        if (previous != null) {
            this.jobVertices.put(id, previous);
            throw new IllegalArgumentException("The JobGraph already contains a vertex with that id.");
        }
    }
}