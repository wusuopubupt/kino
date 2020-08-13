package com.mathandcs.kino.abacus.api.datastream;

import com.mathandcs.kino.abacus.api.operators.OneInputOperator;

public class OneInputDataStream<IN, OUT> extends DataStream<IN> {

    public OneInputDataStream(DataStream<IN> input, OneInputOperator<IN, OUT> operator) {
        super(input, operator);
    }
}
