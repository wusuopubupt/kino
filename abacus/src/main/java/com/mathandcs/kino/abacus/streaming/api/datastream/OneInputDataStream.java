package com.mathandcs.kino.abacus.streaming.api.datastream;

import com.mathandcs.kino.abacus.streaming.api.environment.ExecutionEnvironment;
import com.mathandcs.kino.abacus.streaming.api.operators.OneInputOperator;

public class OneInputDataStream<IN, OUT> extends DataStream<IN> {

    public OneInputDataStream(ExecutionEnvironment env, DataStream<IN> input, OneInputOperator<IN, OUT> operator) {
        super(env, input, operator);
    }

    public OneInputDataStream(DataStream<IN> input, OneInputOperator<IN, OUT> operator) {
        super(input, operator);
    }
}
