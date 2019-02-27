package com.mathandcs.kino.abacus.streaming.api.collector;

import com.mathandcs.kino.abacus.streaming.runtime.record.StreamRecord;

public class TimestampedCollector<T> implements Collector<T> {

    private final Output<StreamRecord<T>> output;

    private final StreamRecord<T> reuse;

    /**
     * Creates a new {@link TimestampedCollector} that wraps the given {@link Output}.
     */
    public TimestampedCollector(Output<StreamRecord<T>> output) {
        this.output = output;
        this.reuse = new StreamRecord<>();
    }

    @Override
    public void collect(T record) {
        output.collect(reuse.replaceValueWith(record));
    }

    public void setTimestamp(StreamRecord<?> timestampBase) {
        if (timestampBase.isHasTimestamp()) {
            reuse.setTimestamp(timestampBase.getTimestamp());
        }
    }

    @Override
    public void close() {
        output.close();
    }
}
