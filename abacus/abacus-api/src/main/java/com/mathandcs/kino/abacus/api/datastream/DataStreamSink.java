package com.mathandcs.kino.abacus.api.datastream;

import com.mathandcs.kino.abacus.api.operators.SinkOperator;

public class DataStreamSink<T> extends AbstractDataStream {

    public DataStreamSink(IDataStream input, SinkOperator<T> sinkOperator) {
        super(input, sinkOperator);
    }

}