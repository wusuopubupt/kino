package com.mathandcs.kino.abacus.streaming.api.operators;

import com.google.common.base.MoreObjects;
import com.mathandcs.kino.abacus.streaming.api.collector.Output;
import com.mathandcs.kino.abacus.streaming.api.context.RuntimeContext;
import com.mathandcs.kino.abacus.streaming.api.functions.Function;
import com.mathandcs.kino.abacus.streaming.runtime.record.StreamRecord;
import java.io.Serializable;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractOperator<OUT, Func extends Function> implements Operator<OUT>, Serializable {

    protected static final Logger LOG = LoggerFactory.getLogger(AbstractOperator.class);

    @Getter
    protected final Func userFunction;

    protected ChainingStrategy chainingStrategy = ChainingStrategy.HEAD;

    protected transient Output<StreamRecord<OUT>> output;

    private transient RuntimeContext runtimeContext;

    public AbstractOperator(Func userFunction) {
        this.userFunction = userFunction;
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
