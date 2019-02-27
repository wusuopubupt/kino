package com.mathandcs.kino.abacus.streaming.api.functions;

@FunctionalInterface
public interface MapFunction<T, R> extends Function {
    R map(T value) throws Exception;
}