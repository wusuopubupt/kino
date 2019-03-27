package com.mathandcs.kino.abacus.streaming.runtime.jobgraph;

import com.mathandcs.kino.abacus.streaming.runtime.io.partition.ResultPartitionType;
import java.util.ArrayList;
import java.util.UUID;
import lombok.Data;

@Data
public class JobVertex {
    private JobVertexID                    id;
    private String                         name;
    private ArrayList<JobEdge>             inputs  = new ArrayList<>();
    private ArrayList<IntermediateDataSet> results = new ArrayList<>();
    private int                            parallelism;

    public JobVertex(JobVertexID id, String name) {
        this.id = id;
        this.name = name;
    }

    public JobEdge connectNewDataSetAsInput(
            JobVertex input,
            DistributionPattern distPattern,
            ResultPartitionType partitionType) {

        IntermediateDataSet dataSet = input.createAndAddResultDataSet(partitionType);

        JobEdge edge = new JobEdge(dataSet, this, distPattern);
        this.inputs.add(edge);
        dataSet.addConsumer(edge);
        return edge;
    }

    public JobEdge connectDataSetAsInput(IntermediateDataSet dataSet, DistributionPattern distPattern) {
        JobEdge edge = new JobEdge(dataSet, this, distPattern);
        this.inputs.add(edge);
        dataSet.addConsumer(edge);
        return edge;
    }

    private IntermediateDataSet createAndAddResultDataSet(ResultPartitionType partitionType) {
        IntermediateDataSet result = new IntermediateDataSet(new IntermediateDataSetID(), partitionType, this);
        this.results.add(result);
        return result;
    }

    public boolean isInputVertex() {
        return this.inputs.isEmpty();
    }

    private boolean isOutputVertex() {
        return this.results.isEmpty();
    }


}