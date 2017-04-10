package com.mathandcs.kino.effectivejava.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Volatile:
 *
 *  1.保证被修饰变量对所有线程的可见性, 但是无法保证被修饰变量在并发下的原子性
 *  2.作为内存屏障,阻止指令重排
 *
 * @author dashwang
 * @date 4/9/17
 */
public class VolatileTest {

    private static final Logger logger = LoggerFactory.getLogger(VolatileTest.class);
    private static volatile boolean initialized = false;

    public static void main(String args[]) {
        final Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // read config file and initialize
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                }
                logger.info("Initialize finished in thread-0");
                initialized = true;
            }
        });
        thread1.start();

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                while(!initialized) {
                    logger.info("Waiting for initializing, sleep 1s");
                    try {
                        Thread.sleep(1000);
                    } catch(InterruptedException e) {
                        logger.error(e.getMessage());
                    }
                }
                // initialized, do something
                System.out.println("Hello, World!");
            }
        });
        thread2.start();
    }
}
