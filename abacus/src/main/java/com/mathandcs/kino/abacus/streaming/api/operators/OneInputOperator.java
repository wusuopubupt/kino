package com.mathandcs.kino.abacus.streaming.api.operators;

import com.mathandcs.kino.abacus.streaming.runtime.record.StreamRecord;
import com.mathandcs.kino.abacus.streaming.runtime.record.Watermark;

/**
 * Interface for stream operators with one input. Use
 */
public interface OneInputOperator<IN, OUT> extends Operator<OUT> {
	void processElement(StreamRecord<IN> element) throws Exception;
	void processWatermark(Watermark mark) throws Exception;
}
