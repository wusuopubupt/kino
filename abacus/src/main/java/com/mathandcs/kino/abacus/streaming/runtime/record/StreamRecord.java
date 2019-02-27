package com.mathandcs.kino.abacus.streaming.runtime.record;

import lombok.Data;
import lombok.Getter;

/**
 * One value in a data stream. This stores the value and an optional associated timestamp.
 * @author dash
 * @param <T> The type encapsulated with the stream record.
 */
@Data
public final class StreamRecord<T> extends StreamElement {

    /** The actual value held by this record. */
    @Getter
    private T value;

    /** The timestamp of the record. */
    @Getter
    private long timestamp;

    /** Flag whether the timestamp is actually set. */
    @Getter
    private boolean hasTimestamp;

    /**
     * Replace the currently stored value by the given new value
     * @param element
     * @param <X>
     * @return
     */
    public <X> StreamRecord<X> replaceValueWith(X element) {
        this.value = (T) element;
        return (StreamRecord<X>) this;
    }
}
