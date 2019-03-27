package com.mathandcs.kino.abacus.streaming.runtime.jobgraph;

import java.io.Serializable;
import lombok.Data;

/**
 * JobEdge:
 *
 * source(IntermediateDataSet) -> target(JobVertex)
 *
 */
@Data
public class JobEdge implements Serializable {

    private IntermediateDataSet source;
    private JobVertex           target;
    private DistributionPattern distributionPattern;

    public JobEdge(IntermediateDataSet source, JobVertex target, DistributionPattern distributionPattern) {
        this.source = source;
        this.target = target;
        this.distributionPattern = distributionPattern;
    }
}
