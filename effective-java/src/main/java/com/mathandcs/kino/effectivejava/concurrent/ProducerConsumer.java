package com.mathandcs.kino.effectivejava.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by wangdongxu on 1/30/18.
 *
 * The Producer and Consumer Model
 *
 * ref:
 * 1. http://javarevisited.blogspot.jp/2015/07/how-to-use-wait-notify-and-notifyall-in.html
 * 2. http://www.importnew.com/27063.html
 *
 */
public class ProducerConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(ProducerConsumer.class);

    private static final int QUEUE_SIZE = 5;

    private static BlockingQueue<Integer> queue = new LinkedBlockingDeque<>(QUEUE_SIZE);

    private static AtomicInteger producedNum = new AtomicInteger(0);

    private static AtomicInteger consumedNum = new AtomicInteger(0);

    private static int max = 20;

    static class Producer extends Thread{

        @Override
        public void run() {
            while (producedNum.intValue() < max) {
                // synchronize shared object
                synchronized (queue) {
                    // wait until queue is not full
                    while (queue.size() == QUEUE_SIZE) {
                        try {
                            queue.wait();
                        } catch (InterruptedException e) {
                            LOG.error("Producer thread interrupted!");
                        }
                    }

                    int i = produce();
                    producedNum.incrementAndGet();
                    System.out.println("Producing #" + producedNum.intValue() +",value: " + i);
                    queue.add(i);
                    queue.notifyAll();
                }
            }
        }

        private int produce() {
            Random random = new Random();
            return random.nextInt();
        }
    }

    static class Consumer extends Thread {
        @Override
        public void run() {
            while (consumedNum.intValue() < max) {
                synchronized (queue) {
                    // wait until queue is not empty
                    while (queue.isEmpty()) {
                        try {
                            queue.wait();
                        } catch (InterruptedException e) {
                            LOG.error("Consumer thread interrupted!");
                        }
                    }

                    consumedNum.incrementAndGet();
                    System.out.println("Consuming #" + consumedNum.intValue() + ",value:" + queue.remove());
                    queue.notifyAll();
                }
            }
        }
    }

    public static void main(String args[]) {
        Thread producer = new Producer();
        Consumer consumer = new Consumer();
        producer.start();
        consumer.start();
    }

}
