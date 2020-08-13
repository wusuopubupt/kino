package com.mathandcs.kino.abacus.runtime.io.channel;

public interface Producer<T> {

    /**
     * produce msg to target channel
     * @param payload
     */
    void produce(T payload, int targetChannelId);

}
