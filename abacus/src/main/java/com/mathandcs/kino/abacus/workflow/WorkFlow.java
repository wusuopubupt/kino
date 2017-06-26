package com.mathandcs.kino.abacus.workflow;

import lombok.Data;

import java.util.List;

/**
 * Created by wangdongxu on 6/23/17.
 */
@Data
public class WorkFlow {
    private int id;
    private List<Node> nodes;

}
