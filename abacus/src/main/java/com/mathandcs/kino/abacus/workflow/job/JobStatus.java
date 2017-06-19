package com.mathandcs.kino.abacus.workflow.job;

/**
 * Created by dashwang on 6/19/17.
 */
public enum JobStatus {
    WAITING(1),
    RUNNING(2),
    SUCCEEDED(3),
    FAILED(4),
    KILLED(5);

    private int value;

    public int getValue() {
        return value;
    }

    JobStatus(int value) {
        this.value = value;
    }

    public static boolean isFinishedStatus(JobStatus status) {
        return (status == JobStatus.SUCCEEDED || status == JobStatus.FAILED || status == JobStatus.KILLED);
    }

    public static boolean isRunningStatus(JobStatus status) {
        return (status == JobStatus.WAITING || status == JobStatus.RUNNING);
    }

}
