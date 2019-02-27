package com.mathandcs.kino.abacus.streaming.api.operators;

import com.mathandcs.kino.abacus.streaming.api.functions.MapFunction;
import com.mathandcs.kino.abacus.streaming.runtime.record.StreamRecord;
import com.mathandcs.kino.abacus.streaming.runtime.record.Watermark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapOperator<T, R> extends AbstractOperator<R, MapFunction<T, R>> implements OneInputOperator<T, R>{

    private static final Logger LOGGER = LoggerFactory.getLogger(MapOperator.class);

    public MapOperator(MapFunction<T, R> mapper) {
        super(mapper);
        this.chainingStrategy = ChainingStrategy.ALWAYS;
    }

    @Override
    public void processElement(StreamRecord<T> record) throws Exception {
        R result = userFunction.map(record.getValue());
        record.replaceValueWith(result);
        output.collect(record.replaceValueWith(result));
    }

    @Override
    public void processWatermark(Watermark mark) throws Exception {

    }
}
