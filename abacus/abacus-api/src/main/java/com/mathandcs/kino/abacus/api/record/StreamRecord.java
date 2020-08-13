package com.mathandcs.kino.abacus.api.record;

import com.mathandcs.kino.abacus.api.datastream.DataStreamId;
import java.io.Serializable;
import lombok.Data;

/**
 * One value in a data stream. This stores the value and an optional associated timestamp.
 * @author dash
 * @param <T> The type encapsulated with the stream record.
 */
@Data
public final class StreamRecord<T> implements Serializable {

    /** The actual value held by this record. */
    private T value;

    /** The timestamp of the record. */
    private long timestamp;

    /* The edge which this record came from. */
    private DataStreamId fromEdgeId;

    public StreamRecord(T value) {
        this.value = value;
    }
}
