package com.mathandcs.kino.abacus.streaming.api.datastream;

import com.google.common.base.MoreObjects;
import com.mathandcs.kino.abacus.streaming.api.environment.ExecutionEnvironment;
import com.mathandcs.kino.abacus.streaming.api.operators.Operator;

public class DataStreamSource<T> extends DataStream<T> {

    public DataStreamSource(ExecutionEnvironment env, DataStream input, Operator operator) {
        super(env, input, operator);
        this.operator = operator;
    }

    public DataStreamSource(ExecutionEnvironment env, Operator operator) {
        super(env, null, operator);
    }

    public DataStreamSource(DataStream<T> input, Operator operator) {
        super(input, operator);
        this.operator = operator;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("operator", operator.getName())
                .add("input", (null == input ? null : input.getId()))
                .toString();
    }
}