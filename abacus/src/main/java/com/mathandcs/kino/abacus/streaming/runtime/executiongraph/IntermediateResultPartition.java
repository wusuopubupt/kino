package com.mathandcs.kino.abacus.streaming.runtime.executiongraph;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class IntermediateResultPartition {

    private IntermediateResult        totalResult;
    private ExecutionVertex           producer;
    private int                       partitionNumber;
    private List<List<ExecutionEdge>> consumers;

    public IntermediateResultPartition(IntermediateResult totalResult, ExecutionVertex producer, int partitionNumber) {
        this.totalResult = totalResult;
        this.producer = producer;
        this.partitionNumber = partitionNumber;
        this.consumers = new ArrayList<List<ExecutionEdge>>(0);
    }

    int addConsumerGroup() {
        int pos = consumers.size();

        // NOTE: currently we support only one consumer per result!!!
        if (pos != 0) {
            throw new RuntimeException("Currently, each intermediate result can only have one consumer.");
        }

        consumers.add(new ArrayList<ExecutionEdge>());
        return pos;
    }

    void addConsumer(ExecutionEdge edge, int consumerNumber) {
        consumers.get(consumerNumber).add(edge);
    }
}
