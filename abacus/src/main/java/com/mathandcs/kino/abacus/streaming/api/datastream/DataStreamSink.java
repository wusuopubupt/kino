package com.mathandcs.kino.abacus.streaming.api.datastream;

import com.mathandcs.kino.abacus.streaming.api.operators.SinkOperator;

public class DataStreamSink<T> implements Transformable {

    private DataStream input;
    private SinkOperator<T> sinkOperator;

    public DataStreamSink(DataStream input, SinkOperator<T> sinkOperator) {
        this.input = input;
        this.sinkOperator = sinkOperator;
    }
}