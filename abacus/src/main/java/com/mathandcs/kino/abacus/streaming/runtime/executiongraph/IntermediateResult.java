package com.mathandcs.kino.abacus.streaming.runtime.executiongraph;

import static com.google.common.base.Preconditions.checkNotNull;

import com.mathandcs.kino.abacus.streaming.runtime.io.partition.ResultPartitionType;
import com.mathandcs.kino.abacus.streaming.runtime.jobgraph.IntermediateDataSetID;

public class IntermediateResult {

	private final IntermediateDataSetID id;
	private final ExecutionJobVertex producer;
	private final IntermediateResultPartition[] partitions;


	private final int numParallelProducers;

	private int numConsumers;

	private final ResultPartitionType resultType;

	public IntermediateResult(
			IntermediateDataSetID id,
			ExecutionJobVertex producer,
			int numParallelProducers,
			ResultPartitionType resultType) {

		this.id = checkNotNull(id);
		this.producer = checkNotNull(producer);
		this.numParallelProducers = numParallelProducers;
		this.partitions = new IntermediateResultPartition[numParallelProducers];
		this.resultType = checkNotNull(resultType);
	}

	public void setPartition(int partitionNumber, IntermediateResultPartition partition) {
		if (partition == null || partitionNumber < 0 || partitionNumber >= numParallelProducers) {
			throw new IllegalArgumentException();
		}

		if (partitions[partitionNumber] != null) {
			throw new IllegalStateException("Partition #" + partitionNumber + " has already been assigned.");
		}

		partitions[partitionNumber] = partition;
	}

	public IntermediateDataSetID getId() {
		return id;
	}

	public ExecutionJobVertex getProducer() {
		return producer;
	}

	public IntermediateResultPartition[] getPartitions() {
		return partitions;
	}

	@Override
	public String toString() {
		return "IntermediateResult " + id.toString();
	}
}
