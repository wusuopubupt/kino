package com.mathandcs.kino.abacus.streaming.runtime.state;

import java.util.Map;

public interface StateBackend<K, V> {
    StateBackend fromConfig(final Map<String, String> config);

    V get(final K key) throws Exception;

    void put(final K key, final V val) throws Exception;

    void remove(final K key) throws Exception;
}