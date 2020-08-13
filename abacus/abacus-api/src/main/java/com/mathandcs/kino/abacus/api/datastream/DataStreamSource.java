package com.mathandcs.kino.abacus.api.datastream;

import com.mathandcs.kino.abacus.api.env.ExecutionEnvironment;
import com.mathandcs.kino.abacus.api.functions.SourceFunction;
import com.mathandcs.kino.abacus.api.functions.source.CollectionSourceFunction;
import com.mathandcs.kino.abacus.api.operators.SourceOperator;
import java.util.Collection;

public class DataStreamSource<T> extends DataStream<T> {

    public DataStreamSource(ExecutionEnvironment env, SourceFunction<T> sourceFunction) {
        super(env, new SourceOperator<>(sourceFunction));
    }

    public static <T> DataStreamSource<T> buildSource(
        ExecutionEnvironment env, Collection<T> values) {
        return new DataStreamSource(env, new CollectionSourceFunction(values));
    }

}