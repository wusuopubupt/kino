package com.mathandcs.kino.abacus.runtime.processor;

import com.mathandcs.kino.abacus.api.env.ExecutionEnvironment;
import com.mathandcs.kino.abacus.api.operators.OneInputOperator;
import com.mathandcs.kino.abacus.api.record.StreamRecord;
import com.mathandcs.kino.abacus.runtime.io.channel.Consumer;
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
    public void setUp(ExecutionEnvironment executionEnvironment) {

    }

    @Override
    public boolean process() throws Exception {
        try {
            StreamRecord record = consumer.poll(1000);
            streamOperator.process(record);
            return true;
        } catch (Exception e) {
            LOG.error("Failed to process input.", e);
            return false;
        }
    }

}
