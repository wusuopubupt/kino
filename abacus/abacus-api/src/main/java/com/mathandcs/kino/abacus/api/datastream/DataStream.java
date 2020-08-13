package com.mathandcs.kino.abacus.api.datastream;

import com.mathandcs.kino.abacus.api.env.ExecutionEnvironment;
import com.mathandcs.kino.abacus.api.functions.FilterFunction;
import com.mathandcs.kino.abacus.api.functions.MapFunction;
import com.mathandcs.kino.abacus.api.functions.SinkFunction;
import com.mathandcs.kino.abacus.api.operators.FilterOperator;
import com.mathandcs.kino.abacus.api.operators.MapOperator;
import com.mathandcs.kino.abacus.api.operators.Operator;
import com.mathandcs.kino.abacus.api.operators.SinkOperator;

/**
 * A DataStream represents a stream of elements of the same type.A DataStream
 *  * can be transformed into another DataStream by applying a transformation as
 *  * for example:
 *  * <ul>
 *  * <li>{@link DataStream#map}
 *  * </ul>
 *  *
 * @param <IN> The type of the elements in this stream
 */
public class DataStream<IN> extends AbstractDataStream {

    public DataStream(ExecutionEnvironment env, Operator operator) {
        super(env, operator);
    }

    public DataStream(DataStream<IN> input, Operator operator) {
        super(input, operator);
    }

    public <OUT> OneInputDataStream<IN, OUT> map(MapFunction<IN, OUT> mapper) {
        OneInputDataStream mapDataStream = new OneInputDataStream(this, new MapOperator(mapper));
        return mapDataStream;
    }

    public <OUT> OneInputDataStream<IN, OUT> filter(FilterFunction<IN> filter) {
        OneInputDataStream filterDataStream = new OneInputDataStream(this, new FilterOperator(filter));
        return filterDataStream;
    }

    public <OUT> DataStreamSink<OUT> sink(SinkFunction<IN> sinkFunction) {
        SinkOperator sinkOperator = new SinkOperator(sinkFunction);
        DataStreamSink sink = new DataStreamSink(this, sinkOperator);
        return sink;
    }

}
