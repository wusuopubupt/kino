package com.mathandcs.kino.abacus.workflow.graph;

import lombok.Data;

/**
 * Created by dashwang on 6/14/17.
 */
@Data
public class Node {

    private int id;
    private String type;

    public Node(int id) {
        this.id = id;
    }

}
