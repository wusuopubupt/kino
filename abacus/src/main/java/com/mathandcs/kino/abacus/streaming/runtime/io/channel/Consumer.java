package com.mathandcs.kino.abacus.streaming.runtime.io.channel;

public interface Consumer<T> {

     /**
      * poll message from up stream
      * @param timeoutMillis
      * @return
      */
     T poll(long timeoutMillis);

}
