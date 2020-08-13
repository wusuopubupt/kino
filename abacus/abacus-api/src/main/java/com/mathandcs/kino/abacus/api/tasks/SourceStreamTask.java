package com.mathandcs.kino.abacus.api.tasks;

import com.mathandcs.kino.abacus.api.functions.SourceFunction;
import com.mathandcs.kino.abacus.api.operators.SourceOperator;

public class SourceStreamTask<OUT, SRC extends SourceFunction<OUT>, OP extends SourceOperator<OUT, SRC>>
        extends StreamTask<OUT, OP> {

    @Override
    protected void init() throws Exception {

    }
}