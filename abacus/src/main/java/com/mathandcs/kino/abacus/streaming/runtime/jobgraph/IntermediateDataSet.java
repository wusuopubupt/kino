package com.mathandcs.kino.abacus.streaming.runtime.jobgraph;

import static com.google.common.base.Preconditions.checkNotNull;

import com.mathandcs.kino.abacus.streaming.runtime.io.partition.ResultPartitionType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class IntermediateDataSet implements Serializable {

	private final IntermediateDataSetID id;
	
	private final JobVertex producer;
	
	private final List<JobEdge> consumers = new ArrayList<JobEdge>();

	// The type of partition to use at runtime
	private final ResultPartitionType resultType;
	
	// --------------------------------------------------------------------------------------------

	public IntermediateDataSet(IntermediateDataSetID id, ResultPartitionType resultType, JobVertex producer) {
		this.id = checkNotNull(id);
		this.producer = checkNotNull(producer);
		this.resultType = checkNotNull(resultType);
	}

    public void addConsumer(JobEdge edge) {
        this.consumers.add(edge);
    }
}
