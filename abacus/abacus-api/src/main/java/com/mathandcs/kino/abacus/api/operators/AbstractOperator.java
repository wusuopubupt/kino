package com.mathandcs.kino.abacus.api.operators;

import com.mathandcs.kino.abacus.api.emitter.Emitter;
import com.mathandcs.kino.abacus.api.env.ExecutionEnvironment;
import com.mathandcs.kino.abacus.api.functions.Function;
import com.mathandcs.kino.abacus.api.record.StreamRecord;
import java.io.Serializable;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractOperator<OUT, Func extends Function> implements Operator<OUT>, Serializable {

    protected static final Logger LOG = LoggerFactory.getLogger(AbstractOperator.class);

    protected Func userFunction;
    protected ExecutionEnvironment env;
    protected List<Emitter> emitters;

    public AbstractOperator(Func userFunction,
                            ExecutionEnvironment env,
                            List<Emitter> emitters) {

        this.userFunction = userFunction;
        this.env = env;
        this.emitters = emitters;
    }

    public AbstractOperator(Func userFunction) {
        this.userFunction = userFunction;
    }

    public ExecutionEnvironment getEnv() {
        return env;
    }

    public List<Emitter> getEmitters() {
        return emitters;
    }

    protected void emit(StreamRecord record) {
        for (Emitter emitter : emitters) {
            emitter.emit(record);
        }
    }

    @Override
    public void open() throws Exception {

    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public String toString() {
        return getName();
    }
}
