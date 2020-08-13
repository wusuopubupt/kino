package com.mathandcs.kino.abacus.api.operators;

import com.mathandcs.kino.abacus.api.common.OperatorName;
import com.mathandcs.kino.abacus.api.functions.FilterFunction;
import com.mathandcs.kino.abacus.api.record.StreamRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilterOperator<T> extends AbstractOperator<T, FilterFunction<T>> implements OneInputOperator<T, T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilterOperator.class);

    public FilterOperator(FilterFunction<T> filter) {
        super(filter);
    }

    @Override
    public void process(StreamRecord<T> record) throws Exception {
        T in = record.getValue();
        if (userFunction.filter(in)) {
           emit(record);
        }
    }

    @Override
    public String getName() {
        return OperatorName.FILTER.toString();
    }
}
