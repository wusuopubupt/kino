package com.mathandcs.kino.abacus.streaming.api.collector;

import com.mathandcs.kino.abacus.streaming.api.collector.Collector;
import com.mathandcs.kino.abacus.streaming.runtime.record.StreamRecord;
import com.mathandcs.kino.abacus.streaming.runtime.record.Watermark;

public interface Output<T> extends Collector<T> {
	void emitWatermark(Watermark mark);
	<X> void collect(long outputId, StreamRecord<X> record);
}
