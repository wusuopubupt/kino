package com.mathandcs.kino.abacus.runtime.processor;

import com.mathandcs.kino.abacus.api.emitter.Emitter;
import com.mathandcs.kino.abacus.api.env.ExecutionEnvironment;
import com.mathandcs.kino.abacus.api.operators.Operator;
import java.util.List;

public abstract class AbstractProcessor<OP extends Operator> implements Processor {

    protected ExecutionEnvironment env;
    protected List<Emitter> emitters;
    protected OP operator;

    public AbstractProcessor(OP operator) {
        this.operator = operator;
    }

    @Override
    public void open(ExecutionEnvironment env, List<Emitter> emitters) throws Exception{
        this.env = env;
        this.emitters = emitters;
        this.operator.open(env, emitters);
    }

}
