package com.mathandcs.kino.abacus.streaming.runtime.record;

/**
 * One value in a data stream. This stores the value and an optional associated timestamp.
 * @author dash
 * @param <T> The type encapsulated with the stream record.
 */
public final class StreamRecord<T> extends StreamElement {

    /** The actual value held by this record. */
    private T value;

    /** The timestamp of the record. */
    private long timestamp;

    /** Flag whether the timestamp is actually set. */
    private boolean hasTimestamp;

    public StreamRecord(T value, long timestamp, boolean hasTimestamp) {
        this.value = value;
        this.timestamp = timestamp;
        this.hasTimestamp = hasTimestamp;
    }

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

    public T getValue() {
        return value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public boolean isHasTimestamp() {
        return hasTimestamp;
    }

}
