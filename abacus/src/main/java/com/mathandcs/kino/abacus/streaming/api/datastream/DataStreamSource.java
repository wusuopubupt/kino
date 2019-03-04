package com.mathandcs.kino.abacus.streaming.api.datastream;

import com.mathandcs.kino.abacus.streaming.api.environment.Environment;
import com.mathandcs.kino.abacus.streaming.api.operators.Operator;

public class DataStreamSource<T> extends DataStream<T> {

    private Operator operator;

    public DataStreamSource(Environment env, Operator operator) {
        this.operator = operator;
    }
}