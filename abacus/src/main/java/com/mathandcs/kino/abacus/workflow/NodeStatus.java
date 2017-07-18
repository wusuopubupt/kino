package com.mathandcs.kino.abacus.workflow;

/**
 * Created by dashwang on 27/10/2016.
 */
public enum NodeStatus {
    SELECTED(5),
    AWAITING(0),
    RUNNING(1),
    SUCCEEDED(2),
    FAILED(3),
    TERMINATED(5);


    private final int value;

    NodeStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

}
