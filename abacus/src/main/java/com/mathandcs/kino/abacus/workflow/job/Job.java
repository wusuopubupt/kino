package com.mathandcs.kino.abacus.workflow.job;

import lombok.Data;

/**
 * Created by dashwang on 6/19/17.
 */
@Data
public abstract class Job {

    private int id;
    private String name;
    private double startProgress;
    private double endProgress;
    private double curProgress;
    private double startTime;
    private double endTime;
    private JobStatus status;

    /**
     * Run the job. In general this method can only be run once. Must either
     * succeed or throw an exception.
     */
    public abstract void run() throws Exception;

    /**
     * Best effort attempt to cancel the job.
     *
     * @throws Exception If cancel fails
     */
    public abstract void cancel() throws Exception;

}
