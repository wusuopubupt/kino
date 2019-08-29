package com.mathandcs.kino.abacus.streaming.runtime.processor;

import com.mathandcs.kino.abacus.streaming.api.operators.SourceOperator;

public class SourceTaskProcessor implements TaskProcessor {

    private final SourceOperator sourceOperator;

    public SourceTaskProcessor(SourceOperator sourceOperator) {
        this.sourceOperator = sourceOperator;
    }

    @Override
    public boolean process() throws Exception {
        sourceOperator.run();
    }
}
