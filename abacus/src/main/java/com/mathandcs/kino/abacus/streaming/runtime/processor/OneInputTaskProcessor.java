package com.mathandcs.kino.abacus.streaming.runtime.processor;

import com.mathandcs.kino.abacus.streaming.api.operators.OneInputOperator;
import com.mathandcs.kino.abacus.streaming.runtime.io.channel.Consumer;
import com.mathandcs.kino.abacus.streaming.runtime.record.StreamElement;
import com.mathandcs.kino.abacus.streaming.runtime.record.StreamRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fetch from input stream and process input elements
 */
public final class OneInputTaskProcessor<IN> implements TaskProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(OneInputTaskProcessor.class);

    private final Consumer<StreamRecord> consumer;
    private final OneInputOperator<IN, ?> streamOperator;

    public OneInputTaskProcessor(Consumer input, OneInputOperator<IN, ?> streamOperator) {
        this.consumer = input;
        this.streamOperator = streamOperator;
    }

    @Override
    public boolean process() throws Exception {
        try {
            StreamRecord element = consumer.poll(1000);
            processElement(element);
            return true;
        } catch (Exception e) {
            LOG.error("Failed to process input.", e);
            return false;
        }
    }

    private void processElement(StreamElement element) throws Exception {
        if (element.isRecord()) {
            // now we can do the actual processing
            StreamRecord<IN> record = element.asRecord();
            streamOperator.processElement(record);
        } else if(element.isWatermark()) {
            // DO NOT SUPPORTED
        }
    }
}
