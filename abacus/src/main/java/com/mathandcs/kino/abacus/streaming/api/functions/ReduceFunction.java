package com.mathandcs.kino.abacus.streaming.api.functions;

import java.io.Serializable;

@FunctionalInterface
public interface ReduceFunction<T> extends Function{
    T reduce(T a, T b) throws Exception;
}
