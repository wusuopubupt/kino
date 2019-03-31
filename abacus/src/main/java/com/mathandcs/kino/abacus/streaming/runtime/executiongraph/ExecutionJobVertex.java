package com.mathandcs.kino.abacus.streaming.runtime.executiongraph;

import com.mathandcs.kino.abacus.streaming.runtime.jobgraph.JobVertex;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class ExecutionJobVertex implements Serializable {
    private ExecutionGraph        graph;
    private JobVertex             jobVertex;
    private List<ExecutionVertex> taskVertices;
    private List<IntermediateResult> produceDataSets;
    private List<IntermediateResult> inputs;
    private int parallelism;
    private int maxParallelism;


}
