package com.mathandcs.kino.effectivejava.concurrent.lock.deadlock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by wangdongxu on 2/2/18.
 *
 * ref: https://www.tutorialspoint.com/java/java_thread_deadlock.htm
 */
public class DeadLockExample {

    private static final Logger LOG  = LoggerFactory.getLogger(DeadLockExample.class);

    public static Object lockA = new Object();
    public static Object lockB = new Object();

    private static class ThreadA extends Thread {
        @Override
        public void run() {
            synchronized (lockA) {
                LOG.info("Thread-A: Holding lock-A");
                try{
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                LOG.info("Thread-A: Waiting for lock-B");
                synchronized (lockB) {
                    LOG.info("Thread-A: Holding locak-B");
                }
            }
        }
    }

    private static class ThreadB extends Thread {
        @Override
        public void run() {
            synchronized (lockB) {
                LOG.info("Thread-B: Holding lock-B");
                try{
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                LOG.info("Thread-B: Waiting for lock-A");
                synchronized (lockA) {
                    LOG.info("Thread-B: Holding locak-A");
                }
            }
        }
    }

    public static void main(String args[]) {
        ThreadA threadA = new ThreadA();
        ThreadB threadB = new ThreadB();
        threadA.start();
        threadB.start();
    }
}
