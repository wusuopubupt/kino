package com.mathandcs.kino.effectivejava.concurrent.synchronize;

import java.util.Date;
import java.util.concurrent.CyclicBarrier;

/**
 * Created by dash wang on 2/7/16.
 *
 * CyclicBarrier可以在构造时指定需要在屏障前执行await的个数，所有对await的调用都会等待，直到调用await的次数达到预定指，所有等待都会立即被唤醒。
 * 从使用场景上来说，CyclicBarrier是让多个线程互相等待某一事件的发生，然后同时被唤醒。
 *
 * 一个形象的例子: 大家(多个线程)约定6:30在麦当劳(栅栏)见面
 */
public class CyclicBarrierDemo {
    public static void main(String[] args) {
        int totalThread = 5;
        CyclicBarrier barrier = new CyclicBarrier(totalThread);

        for (int i = 0; i < totalThread; i++) {
            String threadName = "Thread " + i;
            new Thread(() -> {
                System.out.println(String.format("%s\t%s %s", new Date(), threadName, " is waiting"));
                try {
                    barrier.await();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                System.out.println(String.format("%s\t%s %s", new Date(), threadName, "ended"));
            }).start();
        }
    }
}

// output:
        //Wed Feb 07 19:08:14 CST 2016	Thread 0  is waiting
        //Wed Feb 07 19:08:14 CST 2016	Thread 2  is waiting
        //Wed Feb 07 19:08:14 CST 2016	Thread 4  is waiting
        //Wed Feb 07 19:08:14 CST 2016	Thread 3  is waiting
        //Wed Feb 07 19:08:14 CST 2016	Thread 1  is waiting
        //Wed Feb 07 19:08:14 CST 2016	Thread 1 ended
        //Wed Feb 07 19:08:14 CST 2016	Thread 4 ended
        //Wed Feb 07 19:08:14 CST 2016	Thread 0 ended
        //Wed Feb 07 19:08:14 CST 2016	Thread 2 ended
        //Wed Feb 07 19:08:14 CST 2016	Thread 3 ended
