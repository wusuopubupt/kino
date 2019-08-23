package com.mathandcs.kino.abacus.streaming.api.operators;

import com.mathandcs.kino.abacus.streaming.api.common.OperatorName;
import com.mathandcs.kino.abacus.streaming.api.functions.SinkFunction;
import com.mathandcs.kino.abacus.streaming.runtime.record.StreamRecord;
import com.mathandcs.kino.abacus.streaming.runtime.record.Watermark;

public class SinkOperator<IN> extends AbstractOperator<Object, SinkFunction<IN>>
        implements OneInputOperator<IN, Object> {

    private static final long serialVersionUID = 1L;

    private transient SimpleContext sinkContext;

    private long currentWatermark = Long.MIN_VALUE;

    public SinkOperator(SinkFunction<IN> sinkFunction) {
        super(sinkFunction);
        chainingStrategy = ChainingStrategy.ALWAYS;
    }

    @Override
    public void open() throws Exception {
        super.open();
        this.sinkContext = new SimpleContext<>(System.currentTimeMillis());
    }

    @Override
    public void processElement(StreamRecord<IN> element) throws Exception {
        sinkContext.element = element;
        userFunction.invoke(element.getValue(), sinkContext);
    }

    @Override
    public void processWatermark(Watermark mark) throws Exception {
        this.currentWatermark = mark.getTimestamp();
    }

    private class SimpleContext<IN> implements SinkFunction.Context<IN> {

        private StreamRecord<IN> element;

        private long startTime;

        public SimpleContext(long startTime) {
            this.startTime = startTime;
        }

        @Override
        public long currentProcessingTime() {
            return System.currentTimeMillis() - startTime;
        }

        @Override
        public long currentWatermark() {
            return currentWatermark;
        }

        @Override
        public Long timestamp() {
            if (element.isHasTimestamp()) {
                return element.getTimestamp();
            }
            return null;
        }
    }

    @Override
    public String getName() {
        return OperatorName.SINK.toString();
    }
}
