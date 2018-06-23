/**
 * Alipay.com Inc. Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.mathandcs.kino.effectivejava.concurrent.threadpool;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author dash wang
 */
public class UserDefinedThreadPool {

    /**
     * params:
     *
     * corePoolSize – the number of threads to keep in the pool, even if they are idle, unless allowCoreThreadTimeOut is set
     * maximumPoolSize – the maximum number of threads to allow in the pool
     * keepAliveTime – when the number of threads is greater than the core, this is the maximum time that excess idle threads will wait
     * for new tasks before terminating.
     * unit – the time unit for the keepAliveTime argument
     * workQueue – the queue to use for holding tasks before they are executed. This queue will hold only the Runnable tasks submitted by
     * the execute method.
     * threadFactory – the factory to use when the executor creates a new thread
     * handler – the handler to use when execution is blocked because the thread bounds and queue capacities are reached
     */
    private ExecutorService executorService = new ThreadPoolExecutor(10, 16, 60, TimeUnit.SECONDS,
            new LinkedBlockingDeque<>(10), new ThreadFactory() {
        private volatile AtomicLong inc = new AtomicLong(0);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "user-defined-thread-" + inc.getAndIncrement());
        }
    }, new ThreadPoolExecutor.AbortPolicy());


}