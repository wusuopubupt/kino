package com.mathandcs.kino.abacus.workflow;

import lombok.Data;

/**
 * Created by dashwang on 6/14/17.
 */
@Data
public class Node {

    private int id;
    private String type;
    private NodeStatus status;

    public Node(int id) {
        this.id = id;
    }

    // check if input is ready
    public boolean isInputReady() {
        return false;
    }

}
