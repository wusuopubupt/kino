package com.mathandcs.kino.abacus.api.operators;

import com.mathandcs.kino.abacus.api.functions.SinkFunction;
import com.mathandcs.kino.abacus.api.common.OperatorName;
import com.mathandcs.kino.abacus.api.record.StreamRecord;

public class SinkOperator<IN> extends AbstractOperator<Object, SinkFunction<IN>>
        implements OneInputOperator<IN, Object> {

    private static final long serialVersionUID = 1L;

    public SinkOperator(SinkFunction<IN> sinkFunction) {
        super(sinkFunction);
    }

    @Override
    public void open() throws Exception {
        super.open();
    }

    @Override
    public void process(StreamRecord<IN> element) throws Exception {
        userFunction.sink(element.getValue());
    }

    @Override
    public String getName() {
        return OperatorName.SINK.toString();
    }
}
