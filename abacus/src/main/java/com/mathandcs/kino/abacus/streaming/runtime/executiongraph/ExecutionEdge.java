package com.mathandcs.kino.abacus.streaming.runtime.executiongraph;

import java.io.Serializable;
import lombok.Data;

@Data
public class ExecutionEdge implements Serializable {
    private IntermediateResultPartition source;
    private ExecutionVertex target;
    private int inputIndex;
}