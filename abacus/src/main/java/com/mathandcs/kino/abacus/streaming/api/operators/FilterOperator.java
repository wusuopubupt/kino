package com.mathandcs.kino.abacus.streaming.api.operators;

import com.mathandcs.kino.abacus.streaming.api.functions.FilterFunction;
import com.mathandcs.kino.abacus.streaming.runtime.record.StreamRecord;
import com.mathandcs.kino.abacus.streaming.runtime.record.Watermark;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilterOperator<T> extends AbstractOperator<T, FilterFunction<T>> implements OneInputOperator<T, T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilterOperator.class);

    public FilterOperator(FilterFunction<T> filter) {
        super(filter);
        this.chainingStrategy = ChainingStrategy.ALWAYS;
    }

    @Override
    public void processElement(StreamRecord<T> record) throws Exception {
        T in = record.getValue();
        if (userFunction.filter(in)) {
            output.collect(record);
        }
    }

    @Override
    public void processWatermark(Watermark mark) throws Exception {

    }
}
