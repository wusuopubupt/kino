package com.mathandcs.kino.abacus.streaming.runtime.io.channel;

import com.mathandcs.kino.abacus.streaming.api.common.UniqueId;

public interface Producer<T> {

    /**
     * produce msg to target channel
     * @param msg
     */
    void produce(T msg, UniqueId targetChannelId);

}
