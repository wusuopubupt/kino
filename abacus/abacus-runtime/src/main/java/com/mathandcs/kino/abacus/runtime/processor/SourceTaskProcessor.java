package com.mathandcs.kino.abacus.runtime.processor;


import com.mathandcs.kino.abacus.api.env.ExecutionEnvironment;
import com.mathandcs.kino.abacus.api.operators.SourceOperator;

public class SourceTaskProcessor implements TaskProcessor {

    private final SourceOperator sourceOperator;

    public SourceTaskProcessor(SourceOperator sourceOperator) {
        this.sourceOperator = sourceOperator;
    }

    @Override
    public void setUp(ExecutionEnvironment executionEnvironment) {

    }

    @Override
    public boolean process() throws Exception {
        //sourceOperator.run();
        return true;
    }
}
