package com.mathandcs.kino.abacus.api.functions;

@FunctionalInterface
public interface FilterFunction<T> extends Function {
    boolean filter(T value) throws Exception;
}