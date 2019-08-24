package com.mathandcs.kino.abacus.streaming.api.datastream;

import com.google.common.base.MoreObjects;
import com.mathandcs.kino.abacus.streaming.api.environment.ExecutionEnvironment;
import com.mathandcs.kino.abacus.streaming.api.operators.Operator;
import com.mathandcs.kino.abacus.streaming.api.common.AbstractID;

public class AbstractTransformable implements Transformable {

    protected AbstractID id;
    protected ExecutionEnvironment env;
    protected DataStream input;
    protected Operator   operator;

    @Override
    public AbstractID getId() {
        return id;
    }

    @Override
    public Operator getOperator() {
        return operator;
    }

    @Override
    public Transformable getInput() {
        return input;
    }

    public ExecutionEnvironment getEnv() {
        return env;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", getId())
                .add("input", (null == getInput() ? null : getInput()))
                .add("operator", getOperator().getName())
                .toString();
    }
}
