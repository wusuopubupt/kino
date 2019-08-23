package com.mathandcs.kino.abacus.streaming.api.datastream;

import com.mathandcs.kino.abacus.streaming.api.environment.ExecutionEnvironment;
import com.mathandcs.kino.abacus.streaming.api.operators.SourceOperator;

public class DataStreamSource<T> extends DataStream<T> {

    public DataStreamSource(ExecutionEnvironment env, DataStream input, SourceOperator operator) {
        super(env, input, operator);
        this.operator = operator;
    }

}