package com.mathandcs.kino.abacus.streaming.api.operators;

import com.mathandcs.kino.abacus.streaming.runtime.record.StreamRecord;

public interface TwoInputOperator<IN1, IN2, OUT> extends Operator<OUT> {

    /**
     * Processes one element that arrived on the first input of this two-input operator.
     * This method is guaranteed to not be called concurrently with other methods of the operator.
     */
    void processElement1(StreamRecord<IN1> element) throws Exception;

    /**
     * Processes one element that arrived on the second input of this two-input operator.
     * This method is guaranteed to not be called concurrently with other methods of the operator.
     */
    void processElement2(StreamRecord<IN2> element) throws Exception;

}