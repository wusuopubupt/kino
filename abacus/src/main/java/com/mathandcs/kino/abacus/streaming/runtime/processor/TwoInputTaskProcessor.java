package com.mathandcs.kino.abacus.streaming.runtime.processor;

import com.mathandcs.kino.abacus.streaming.api.common.UniqueId;
import com.mathandcs.kino.abacus.streaming.api.operators.TwoInputOperator;
import com.mathandcs.kino.abacus.streaming.runtime.io.channel.Consumer;
import com.mathandcs.kino.abacus.streaming.runtime.record.StreamElement;
import com.mathandcs.kino.abacus.streaming.runtime.record.StreamRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwoInputTaskProcessor<IN1, IN2> implements TaskProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(TwoInputTaskProcessor.class);

    private final TwoInputOperator<IN1, IN2, ?> streamOperator;
    private final Consumer<StreamElement> consumer;
    private final UniqueId leftInputEdgeId;
    private final UniqueId rightInputEdgeId;

    public TwoInputTaskProcessor(
            TwoInputOperator<IN1, IN2, ?> streamOperator,
            Consumer<StreamElement> consumer,
            UniqueId leftInputEdgeId,
            UniqueId rightInputEdgeId) {
        this.streamOperator = streamOperator;
        this.consumer = consumer;
        this.leftInputEdgeId = leftInputEdgeId;
        this.rightInputEdgeId = rightInputEdgeId;
    }

    @Override
    public boolean process() throws Exception {
        try {
            StreamElement element = consumer.poll(1000);
            processElement(element);
            return true;
        } catch (Exception e) {
            LOG.error("Failed to process input.", e);
            return false;
        }
    }

    public void processElement(StreamElement element) throws Exception{
        if (element.isRecord()) {
            if (element.getFromEdgeId() == leftInputEdgeId) {
                StreamRecord<IN1> record = element.asRecord();
                streamOperator.processElement1(record);
            } else if (element.getFromEdgeId() == rightInputEdgeId) {
                StreamRecord<IN2> record = element.asRecord();
                streamOperator.processElement2(record);
            } else {
                throw new IllegalArgumentException("Unknown input edge, element: " + element);
            }
        } else if (element.isWatermark()) {
            LOG.error("Can not process watermark.");
        } else {
            throw new IllegalArgumentException("Unkown element: " + element);
        }
    }

}
