package com.mathandcs.kino.abacus.runtime.processor;

import com.mathandcs.kino.abacus.api.operators.OneInputOperator;
import com.mathandcs.kino.abacus.api.record.StreamRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OneInputProcessor extends AbstractProcessor<OneInputOperator> {

    private static final Logger LOG = LoggerFactory.getLogger(OneInputProcessor.class);

    public OneInputProcessor(OneInputOperator operator) {
        super(operator);
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
