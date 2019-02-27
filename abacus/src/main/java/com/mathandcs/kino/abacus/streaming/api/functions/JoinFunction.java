package com.mathandcs.kino.abacus.streaming.api.functions;

/**
 * Usage:
 *
 * set1.join(set2).where(<key-definition>).equalTo(<key-definition>).with(new MyJoinFunction())
 *
 */
@FunctionalInterface
public interface JoinFunction<IN1, IN2, OUT> extends Function {
    OUT join(IN1 left, IN2 right) throws Exception;
}