package com.mathandcs.kino.abacus.streaming.runtime.executiongraph;

import java.io.Serializable;
import lombok.Data;

@Data
public class JobInformation implements Serializable {
    private String jobName;

    public JobInformation(String jobName) {
        this.jobName = jobName;
    }
}