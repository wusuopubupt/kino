package com.mathandcs.kino.abacus.workflow;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dashwang on 6/14/17.
 */
@Data
public class DAG {

    private int id;

    private List<Node> allNodes = new ArrayList<>();
    private List<Node> startNodes;
    private List<Node> endNodes;
    private List<Node> runNodes;

    private static final Logger LOGGER = LoggerFactory.getLogger(DAG.class);

    /**
     * 0   1
     * 2
     * 3   4
     * <p>
     * {
     * "input_edges" : {
     * 2 : 0,
     * 2 : 1,
     * 3 : 2,
     * 4 : 3
     * }
     * "output_edges" : {
     * 0 : 0,
     * 1 : 1,
     * 2 : 2,3
     * }
     * "nodes" : [0,1,2,3,4]
     * },
     * "edges": [
     * [0,2],
     * [1,2],
     * [2,3],
     * [2,4]
     * ]
     * }
     * }
     */
    public void initialize() {
        if (this.startNodes == null) {
            this.startNodes = new ArrayList<>();
            this.endNodes = new ArrayList<>();

        }
    }

    /**
     * Build DAG structure from input object
     *
     * @param flowObject
     * @return DAG
     */
    public DAG loadDAGFromObject(JsonObject flowObject) {
        DAG dag = new DAG();

        // Loading nodes
        JsonArray nodeList = flowObject.get("nodes").getAsJsonArray();
        return dag;
    }
}
