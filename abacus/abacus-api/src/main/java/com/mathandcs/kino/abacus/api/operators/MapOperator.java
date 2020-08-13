package com.mathandcs.kino.abacus.api.operators;

import com.mathandcs.kino.abacus.api.common.OperatorName;
import com.mathandcs.kino.abacus.api.functions.MapFunction;
import com.mathandcs.kino.abacus.api.record.StreamRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapOperator<T, R> extends AbstractOperator<R, MapFunction<T, R>> implements OneInputOperator<T, R>{

    private static final Logger LOG = LoggerFactory.getLogger(MapOperator.class);

    public MapOperator(MapFunction<T, R> mapper) {
        super(mapper);
    }

    @Override
    public void process(StreamRecord<T> record) throws Exception {
        R result = userFunction.map(record.getValue());
        emit(new StreamRecord(result));
    }

    @Override
    public String getName() {
        return OperatorName.MAP.toString();
    }
}
