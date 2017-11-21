package com.mathandcs.kino.abacus.workflow;

import lombok.Data;

import java.util.List;

/**
 * Created by dashwang on 6/23/17.
 */
@Data
public class WorkFlow {
    private int id;
    private List<Node> nodes;

}
