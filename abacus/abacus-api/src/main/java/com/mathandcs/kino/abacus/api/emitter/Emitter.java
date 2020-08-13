package com.mathandcs.kino.abacus.api.emitter;

/**
 * Record Emitter.
 */
public interface Emitter<T> {

    /**
     * Emits a record.
     *
     * @param record The record to be emitted.
     */
    void emit(T record);

    /**
     * Closes the emitter. If any data was buffered, that data will be flushed.
     */
    void close();
}
