package com.mathandcs.kino.abacus.streaming.runtime.executiongraph;

import java.io.Serializable;
import java.util.Map;
import lombok.Data;

@Data
public class ExecutionVertex implements Serializable {
	private final ExecutionJobVertex                                              jobVertex;
	private final ExecutionEdge[][]                                               inputEdges;
	private final Map<IntermediateResultPartitionID, IntermediateResultPartition> resultPartitions;
	private final int                                                             subTaskIndex;
	private final String                                                          taskNameWithSubtask;

}
