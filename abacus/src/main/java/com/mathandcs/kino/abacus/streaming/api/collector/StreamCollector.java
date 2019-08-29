package com.mathandcs.kino.abacus.streaming.api.collector;


import com.mathandcs.kino.abacus.streaming.runtime.io.writer.RecordWriter;
import com.mathandcs.kino.abacus.streaming.runtime.record.StreamRecord;

public class StreamCollector<T> implements Collector<StreamRecord<T>> {

    private final RecordWriter recordWriter;

    @Override
    public void collect(StreamRecord<T> record) {

    }

    @Override
    public void close() {

    }
}
