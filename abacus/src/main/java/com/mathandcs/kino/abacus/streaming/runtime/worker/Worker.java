package com.mathandcs.kino.abacus.streaming.runtime.worker;

import com.mathandcs.kino.abacus.streaming.runtime.record.StreamElement;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * A worker is a jvm process which holds a mailbox that can receive message from other worker
 *
 */
public class Worker {

    private final String host;
    private final int port;
    private final Thread receiverThread;
    private final ArrayBlockingQueue<StreamElement> inputQueue = new ArrayBlockingQueue<>(512);
    private final ArrayBlockingQueue<StreamElement> outputQueue = new ArrayBlockingQueue<>(512);




}
