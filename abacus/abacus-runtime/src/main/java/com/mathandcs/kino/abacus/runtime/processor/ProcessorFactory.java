package com.mathandcs.kino.abacus.runtime.processor;

import com.mathandcs.kino.abacus.api.operators.OneInputOperator;
import com.mathandcs.kino.abacus.api.operators.Operator;
import com.mathandcs.kino.abacus.api.operators.SourceOperator;

public class ProcessorFactory {

    public static Processor createProcessor(Operator operator) {
        if (operator instanceof SourceOperator) {
            return new SourceProcessor((SourceOperator) operator);
        } else if (operator instanceof OneInputOperator) {
            return new OneInputProcessor((OneInputOperator) operator);
        } else {
          throw new IllegalArgumentException("Unsupported operator: " + operator);
        }
    }
}
