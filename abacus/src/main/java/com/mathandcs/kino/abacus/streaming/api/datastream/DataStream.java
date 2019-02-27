package com.mathandcs.kino.abacus.streaming.api.datastream;

import com.mathandcs.kino.abacus.streaming.api.functions.FilterFunction;
import com.mathandcs.kino.abacus.streaming.api.functions.FlatMapFunction;
import com.mathandcs.kino.abacus.streaming.api.functions.MapFunction;
import com.mathandcs.kino.abacus.streaming.api.operators.FilterOperator;
import com.mathandcs.kino.abacus.streaming.api.operators.FlatMapOperator;
import com.mathandcs.kino.abacus.streaming.api.operators.MapOperator;
import com.mathandcs.kino.abacus.streaming.api.operators.Operator;

/**
 * A DataStream represents a stream of elements of the same type.A DataStream
 *  * can be transformed into another DataStream by applying a transformation as
 *  * for example:
 *  * <ul>
 *  * <li>{@link DataStream#map}
 *  * </ul>
 *  *
 * @param <T> The type of the elements in this stream
 */
public class DataStream<T> {

    protected DataStream input;
    protected Operator   operator;

    public DataStream() {
    }

    public DataStream(DataStream<T> input, Operator operator) {
        this.input = input;
        this.operator = operator;
    }

    public <R> DataStream<R> map(MapFunction<T, R> mapper) {
        return new DataStream(this, new MapOperator(mapper));
    }

    public DataStream<T> filter(FilterFunction<T> filter) {
        return new DataStream(this, new FilterOperator(filter));
    }

    public <R> DataStream<R> flatMap(FlatMapFunction<T, R> flatMapper) {
        return new DataStream(this, new FlatMapOperator(flatMapper));
    }

}