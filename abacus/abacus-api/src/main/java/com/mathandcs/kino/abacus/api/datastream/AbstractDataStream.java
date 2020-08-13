package com.mathandcs.kino.abacus.api.datastream;

import com.google.common.base.MoreObjects;
import com.mathandcs.kino.abacus.api.env.ExecutionEnvironment;
import com.mathandcs.kino.abacus.api.operators.Operator;
import com.mathandcs.kino.abacus.core.io.Partitioner;

public class AbstractDataStream<T> implements IDataStream<T> {

    protected ExecutionEnvironment env;
    protected DataStreamId id;
    protected IDataStream<T> input;
    protected Operator operator;
    protected int parallelism;
    protected Partitioner<T> partitioner;

    public AbstractDataStream(IDataStream<T> input,
                              Operator operator) {

        this.env = input.getEnv();
        this.input = input;
        this.operator = operator;
        this.id = new DataStreamId(env.getDataStreams().size() + 1, operator.getName());
        this.env.addDataStream(this);
    }

    public AbstractDataStream(ExecutionEnvironment env, Operator operator) {
        this.env = env;
        this.operator = operator;
        this.id = new DataStreamId(env.getDataStreams().size() + 1, operator.getName());
        this.env.addDataStream(this);
    }

    @Override
    public DataStreamId getId() {
        return id;
    }

    @Override
    public Operator getOperator() {
        return operator;
    }

    @Override
    public IDataStream<T> getInput() {
        return input;
    }

    @Override
    public int getParallelism() {
        return parallelism;
    }

    @Override
    public Partitioner<T> getPartitioner() {
        return partitioner;
    }

    public ExecutionEnvironment getEnv() {
        return env;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("env", env)
            .add("id", id)
            .add("input", input)
            .add("operator", operator)
            .toString();
    }
}
