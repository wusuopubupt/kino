package com.mathandcs.kino.abacus.streaming.api.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class ExecutionConfig implements Serializable {
    private int parallelism = 1;
    private int maxParallelism = 1024;
}
