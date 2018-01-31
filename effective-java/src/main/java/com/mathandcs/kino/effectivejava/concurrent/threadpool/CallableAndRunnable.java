
package com.mathandcs.kino.effectivejava.concurrent.threadpool;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by dashwang on 12/8/17.
 */
public class CallableAndRunnable {

    //ExecutorService threadPool = Executors.newSingleThreadExecutor();
    //ExecutorService threadPool = Executors.newFixedThreadPool(2);
    //ExecutorService threadPool = Executors.newScheduledThreadPool(2);
    ExecutorService threadPool = Executors.newCachedThreadPool();

    // delegate methods
    public void shutdown() {
        threadPool.shutdown();
    }

    public <T> Future<T> submit(Callable<T> task) {
        return threadPool.submit(task);
    }

    public Future<?> submit(Runnable task) {
        return threadPool.submit(task);
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return threadPool.invokeAny(tasks);
    }

    public boolean isShutdown() {
        return threadPool.isShutdown();
    }

    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return threadPool.invokeAny(tasks, timeout, unit);
    }

    public boolean isTerminated() {
        return threadPool.isTerminated();
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return threadPool.invokeAll(tasks);
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return threadPool.awaitTermination(timeout, unit);
    }

    public void execute(Runnable command) {
        threadPool.execute(command);
    }

    public <T> Future<T> submit(Runnable task, T result) {
        return threadPool.submit(task, result);
    }

    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return threadPool.invokeAll(tasks, timeout, unit);
    }

    public List<Runnable> shutdownNow() {
        return threadPool.shutdownNow();
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException{
        CallableAndRunnable app = new CallableAndRunnable();

        // thread-1
        app.threadPool.submit(() -> {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                System.out.println("thread interrupted!");
            }
            for (int i = 0; i < 100; i++) {
                System.out.println("run: " + i);
            }
        });

        // thread-2
        Future<Integer> future = app.threadPool.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                int i = 0;
                for (i = 0; i < 5; i++) {
                    System.out.println("call: " + i);
                }
                return i;
            }
        });

        try {
            int result = future.get();
            System.out.println("result is: " + result + ", future is done? -> " + future.isDone());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        boolean isTerminated = app.threadPool.isTerminated();
        System.out.println(isTerminated);

        // wait 3 seconds
        app.threadPool.awaitTermination(3, TimeUnit.SECONDS);
        app.threadPool.shutdownNow();
    }
}
