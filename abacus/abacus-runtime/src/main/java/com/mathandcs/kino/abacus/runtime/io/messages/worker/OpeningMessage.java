package com.mathandcs.kino.abacus.runtime.io.messages.worker;

import com.mathandcs.kino.abacus.api.plan.logical.LogicalNode;

public class OpeningMessage implements WorkerMessage {

    private LogicalNode node;

    public OpeningMessage(LogicalNode node) {
        this.node = node;
    }

    public LogicalNode getLogicalNode() {
        return node;
    }

}
