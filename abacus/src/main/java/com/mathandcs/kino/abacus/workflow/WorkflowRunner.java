package com.mathandcs.kino.abacus.workflow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dashwang on 6/15/17.
 */
public class WorkflowRunner implements Runnable {

    @Override
    public void run() {
    }

    private List<Node> getSelectedNodes() {
        return null;
    }

    //
    private List<Node> getReadyNodes(List<Node> selectedNodes) {
        List<Node> readyNodes = new ArrayList<>();
        for (Node node : selectedNodes) {
            if (node.isInputReady()) {
                readyNodes.add(node);
            }
        }
        return readyNodes;
    }

    private void setNodeStatus() {

    }


}
