package com.mathandcs.kino.abacus.streaming.runtime.record;

/**
 * An element in a data stream. Can be a record or a Watermark.
 */
public abstract class StreamElement {

    /**
     * Checks whether this element is a watermark.
     * @return True, if this element is a watermark, false otherwise.
     */
    public final boolean isWatermark() {
        return getClass() == Watermark.class;
    }

    /**
     * Checks whether this element is a record.
     * @return True, if this element is a record, false otherwise.
     */
    public final boolean isRecord() {
        return getClass() == StreamRecord.class;
    }

    /**
     * Casts this element into a StreamRecord.
     * @return This element as a stream record.
     * @throws ClassCastException Thrown, if this element is actually not a stream record.
     */
    @SuppressWarnings("unchecked")
    public final <E> StreamRecord<E> asRecord() {
        return (StreamRecord<E>) this;
    }

    /**
     * Casts this element into a Watermark.
     * @return This element as a Watermark.
     * @throws ClassCastException Thrown, if this element is actually not a Watermark.
     */
    public final Watermark asWatermark() {
        return (Watermark) this;
    }
}
