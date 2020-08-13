package com.mathandcs.kino.abacus.api.functions;

@FunctionalInterface
public interface ReduceFunction<T> extends Function{
    T reduce(T a, T b) throws Exception;
}
