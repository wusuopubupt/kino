package com.mathandcs.kino.abacus.streaming.api.functions;

import com.mathandcs.kino.abacus.streaming.api.collector.Collector;

@FunctionalInterface
public interface FlatMapFunction<T, R> extends Function {
    void flatMap(T value, Collector<R> result) throws Exception;
}