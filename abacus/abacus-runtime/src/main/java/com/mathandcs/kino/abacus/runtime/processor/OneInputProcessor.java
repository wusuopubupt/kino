package com.mathandcs.kino.abacus.runtime.processor;

import com.mathandcs.kino.abacus.api.operators.OneInputOperator;
import com.mathandcs.kino.abacus.api.record.StreamRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OneInputProcessor extends AbstractProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(OneInputProcessor.class);

    private OneInputOperator operator;

    public OneInputProcessor(OneInputOperator operator) {
        this.operator = operator;
    }

    @Override
  	public void process(StreamRecord record) {
        try {
            operator.process(record);
        } catch (Exception e) {
            LOG.error("Failed to process record: {}.", record, e);
        }
  	}
}
