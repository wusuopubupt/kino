package com.mathandcs.kino.abacus.streaming.runtime.io.partition;

import com.google.common.base.Preconditions;
import com.mathandcs.kino.abacus.streaming.api.functions.KeySelector;

/**
 * Partitioner selects the target channel based on the key group index.
 */
public class KeyGroupStreamPartitioner<T, K> extends StreamPartitioner<T> {
    private static final long serialVersionUID = 1L;

    private final int[] returnArray = new int[1];

    private final KeySelector<T, K> keySelector;

    public KeyGroupStreamPartitioner(KeySelector<T, K> keySelector) {
        this.keySelector = Preconditions.checkNotNull(keySelector);
    }

    @Override
    public int[] selectChannels(T record) {
        K key;
        try {
            key = keySelector.getKey(record);
        } catch (Exception e) {
            throw new RuntimeException("Could not extract key from " + record, e);
        }
        returnArray[0] = key.hashCode() % numberOfChannels;
        return returnArray;
    }

    @Override
    public StreamPartitioner<T> copy() {
        return this;
    }

    @Override
    public String toString() {
        return "HASH";
    }
}
