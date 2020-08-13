package com.mathandcs.kino.abacus.api.functions;

@FunctionalInterface
public interface MapFunction<T, R> extends Function {
    R map(T value) throws Exception;
}