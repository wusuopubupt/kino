package com.mathandcs.kino.abacus.api.emitter;

import com.mathandcs.kino.abacus.api.record.StreamRecord;
import com.mathandcs.kino.abacus.core.io.RecordWriter;

public class StreamEmitter<T> implements Emitter<StreamRecord<T>> {

    private final RecordWriter recordWriter;

    public StreamEmitter(RecordWriter recordWriter) {
        this.recordWriter = recordWriter;
    }

    @Override
    public void emit(StreamRecord<T> record) {
        this.recordWriter.write(record);
    }

    @Override
    public void close() {

    }

}
