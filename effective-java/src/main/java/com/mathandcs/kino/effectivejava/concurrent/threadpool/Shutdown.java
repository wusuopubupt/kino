/**
 * Alipay.com Inc. Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.mathandcs.kino.effectivejava.concurrent.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Shutdown用法总结
 */
public class Shutdown {

    private static volatile boolean stopR1 = false;

    public static void main(String[] args) {
        ExecutorService service = Executors.newFixedThreadPool(2);

        // shutdownNow在while循环里不会终止
        Runnable r1 = new Runnable() {
            @Override
            public void run() {
                while (!stopR1) {
                    System.out.println("r1");
                }
            }
        };

        // 需要加上判断当前线程是否已经终止
        Runnable r2 = () -> {
            long sum = 0;
            boolean flag = true;
            while (flag && !Thread.currentThread().isInterrupted()) {
                sum += 1;
                if (sum == Long.MAX_VALUE) { flag = false; }
                System.out.println("r2");
            }
        };

        service.execute(r1);
        service.execute(r2);
        service.shutdown();

        try {
            if (!service.awaitTermination(2, TimeUnit.SECONDS)) {
                service.shutdownNow();
            }
        } catch (InterruptedException e) {
            service.shutdownNow();
        }
        // shutdownNow对r1不起作用

        try {
            Thread.sleep(1000);
        } catch(Exception e) {
            // do nothing
        }

        // 这时候r1才终止
        stopR1 = true;
    }

}