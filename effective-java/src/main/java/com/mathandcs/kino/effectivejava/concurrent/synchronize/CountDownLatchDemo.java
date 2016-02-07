package com.mathandcs.kino.effectivejava.concurrent.synchronize;

/**
 * Created by dash wang on 2/7/16.
 */

import java.util.Date;
import java.util.concurrent.CountDownLatch;

/**
 * 类似Golang的waitGroup
 *
 * Java多线程编程中经常会碰到这样一种场景——某个线程需要等待一个或多个线程操作结束（或达到某种状态）才开始执行。
 * 比如开发一个并发测试工具时，主线程需要等到所有测试线程均执行完成再开始统计总共耗费的时间，此时可以通过CountDownLatch轻松实现。
 *
 *
 * 于Thread.join()的区别: countDownLatch不需要等待线程结束(只要wait count == 0)就让主线程工作
 *
 * 与CyclicBarrier的区别: countDownLatch不能被重置
 */
public class CountDownLatchDemo {
    public static void main(String[] args) throws InterruptedException {
        int totalThread = 3;
        long start = System.currentTimeMillis();
        // initialize wait group
        CountDownLatch countDown = new CountDownLatch(totalThread);
        for (int i = 0; i < totalThread; i++) {
            final String threadName = "Thread " + i;
            new Thread(() -> {
                System.out.println(String.format("%s\t%s %s", new Date(), threadName, "started"));
                try {
                    Thread.sleep(1000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                // wait group -= 1
                countDown.countDown();
                System.out.println(String.format("%s\t%s %s", new Date(), threadName, "ended"));
            }).start();
            ;
        }
        // wait for all wait group done
        countDown.await();
        long stop = System.currentTimeMillis();
        System.out.println(String.format("Total time : %sms", (stop - start)));
    }
}

// output :
//        Wed Feb 07 18:20:45 CST 2016	Thread 2 started
//        Wed Feb 07 18:20:45 CST 2016	Thread 0 started
//        Wed Feb 07 18:20:45 CST 2016	Thread 1 started
//        Wed Feb 07 18:20:46 CST 2016	Thread 2 ended
//        Wed Feb 07 18:20:46 CST 2016	Thread 1 ended
//        Wed Feb 07 18:20:46 CST 2016	Thread 0 ended
//        Total time : 1400ms
