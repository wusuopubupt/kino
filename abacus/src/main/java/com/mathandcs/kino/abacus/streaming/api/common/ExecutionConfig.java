package com.mathandcs.kino.abacus.streaming.api.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class ExecutionConfig implements Serializable {
    private String jobName;
    private RunMode runMode;
    private int parallelism = 1;
    private int maxParallelism = 1024;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public int getParallelism() {
        return parallelism;
    }

    public void setParallelism(int parallelism) {
        this.parallelism = parallelism;
    }

    public int getMaxParallelism() {
        return maxParallelism;
    }

    public void setMaxParallelism(int maxParallelism) {
        this.maxParallelism = maxParallelism;
    }

    public RunMode getRunMode() {
        return runMode;
    }

    public void setRunMode(RunMode runMode) {
        this.runMode = runMode;
    }
}
