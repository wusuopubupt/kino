package com.mathandcs.kino.abacus.streaming.api.graph.tasks;

import com.mathandcs.kino.abacus.streaming.api.functions.SourceFunction;
import com.mathandcs.kino.abacus.streaming.api.operators.SourceOperator;

public class SourceStreamTask<OUT, SRC extends SourceFunction<OUT>, OP extends SourceOperator<OUT, SRC>>
        extends StreamTask<OUT, OP> {

    @Override
    protected void init() throws Exception {

    }
}