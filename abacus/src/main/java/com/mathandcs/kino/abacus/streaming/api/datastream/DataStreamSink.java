package com.mathandcs.kino.abacus.streaming.api.datastream;

import com.mathandcs.kino.abacus.streaming.api.operators.SinkOperator;
import com.mathandcs.kino.abacus.streaming.api.common.UniqueId;

public class DataStreamSink<T> extends AbstractTransformable {

    public DataStreamSink(DataStream input, SinkOperator<T> sinkOperator) {
        this.id = new UniqueId();
        this.input = input;
        this.operator = sinkOperator;
    }

}