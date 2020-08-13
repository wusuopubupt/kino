package com.mathandcs.kino.abacus.api.operators;

import com.mathandcs.kino.abacus.api.record.StreamRecord;

/**
 * Interface for stream operators with one input.
 */
public interface OneInputOperator<IN, OUT> extends Operator<OUT> {

	void process(StreamRecord<IN> record) throws Exception;

}
