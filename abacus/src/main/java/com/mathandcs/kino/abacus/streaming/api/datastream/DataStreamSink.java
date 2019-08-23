package com.mathandcs.kino.abacus.streaming.api.datastream;

import com.mathandcs.kino.abacus.streaming.api.operators.Operator;
import com.mathandcs.kino.abacus.streaming.api.operators.SinkOperator;
import com.mathandcs.kino.abacus.streaming.runtime.utils.AbstractID;

public class DataStreamSink<T> implements Transformable {

    private AbstractID id;
    private DataStream input;
    private SinkOperator<T> sinkOperator;

    public DataStreamSink(DataStream input, SinkOperator<T> sinkOperator) {
        this.id = new AbstractID();
        this.input = input;
        this.sinkOperator = sinkOperator;
    }

    @Override
    public AbstractID getId() {
        return id;
    }

    @Override
    public Operator getOperator() {
        return sinkOperator;
    }

    @Override
    public Transformable getInput() {
        return input;
    }
}