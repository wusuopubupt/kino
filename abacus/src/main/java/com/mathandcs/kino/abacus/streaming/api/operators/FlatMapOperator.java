package com.mathandcs.kino.abacus.streaming.api.operators;

import com.mathandcs.kino.abacus.streaming.api.collector.TimestampedCollector;
import com.mathandcs.kino.abacus.streaming.api.functions.FlatMapFunction;
import com.mathandcs.kino.abacus.streaming.runtime.record.StreamRecord;
import com.mathandcs.kino.abacus.streaming.runtime.record.Watermark;

public class FlatMapOperator<IN, OUT> extends AbstractOperator<OUT, FlatMapFunction<IN, OUT>> implements OneInputOperator<IN, OUT> {

    private transient TimestampedCollector<OUT> collector;

    public FlatMapOperator(FlatMapFunction<IN, OUT> flatMapper) {
        super(flatMapper);
        chainingStrategy = ChainingStrategy.ALWAYS;
    }

    @Override
    public void open() throws Exception {
        super.open();
        collector = new TimestampedCollector<>(output);
    }

    @Override
    public void processElement(StreamRecord<IN> element) throws Exception {
        userFunction.flatMap(element.getValue(), collector);
    }

    public void processWatermark(Watermark mark) throws Exception {

    }
}
