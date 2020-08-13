package com.mathandcs.kino.abacus.api.datastream;

import com.mathandcs.kino.abacus.api.env.ExecutionEnvironment;
import com.mathandcs.kino.abacus.api.operators.Operator;
import com.mathandcs.kino.abacus.core.io.Partitioner;
import java.io.Serializable;

/**
 * The basic DataStream interface.
 */
public interface IDataStream<T> extends Serializable {

    DataStreamId getId();

    Operator getOperator();

    IDataStream<T> getInput();

    int getParallelism();

    Partitioner<T> getPartitioner();

    ExecutionEnvironment getEnv();

}
