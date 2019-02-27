package com.mathandcs.kino.abacus.streaming.runtime.record;

import lombok.Data;

/**
 * A Watermark tells operators that no elements with a timestamp older or equal
 * to the watermark timestamp should arrive at the operator. Watermarks are emitted at the
 * sources and propagate through the operators of the topology. Operators must themselves emit
 * watermarks to downstream operators using
 * {Watermark}. Operators that
 * do not internally buffer elements can always forward the watermark that they receive. Operators
 * that buffer elements, such as window operators, must forward a watermark after emission of
 * elements that is triggered by the arriving watermark.
 *
 * <p>In some cases a watermark is only a heuristic and operators should be able to deal with
 * late elements. They can either discard those or update the result and emit updates/retractions
 * to downstream operations.
 *
 * <p>When a source closes it will emit a final watermark with timestamp {@code Long.MAX_VALUE}.
 * When an operator receives this it will know that no more input will be arriving in the future.
 */
@Data
public final class Watermark extends StreamElement {

    /** The watermark that signifies end-of-event-time. */
    public static final Watermark MAX_WATERMARK = new Watermark(Long.MAX_VALUE);

    // ------------------------------------------------------------------------

    /** The timestamp of the watermark in milliseconds. */
    private final long timestamp;

    /**
     * Creates a new watermark with the given timestamp in milliseconds.
     */
    public Watermark(long timestamp) {
        this.timestamp = timestamp;
    }

}
