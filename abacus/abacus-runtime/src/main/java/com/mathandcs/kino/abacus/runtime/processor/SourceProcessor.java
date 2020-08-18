package com.mathandcs.kino.abacus.runtime.processor;

import com.mathandcs.kino.abacus.api.operators.SourceOperator;
import com.mathandcs.kino.abacus.api.record.StreamRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SourceProcessor extends AbstractProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(SourceProcessor.class);

    private SourceOperator operator;

    public SourceProcessor(SourceOperator operator) {
        this.operator = operator;
    }

    @Override
    public void process(StreamRecord record) {
        try {
            operator.run();
        } catch (Exception e) {
            LOG.error("Failed to run source operator.", e);
        }
    }

}
