package com.mathandcs.kino.effectivejava.concurrent.threadLocal;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by dashwang on 12/8/17.
 */
public class MainApp {

    private static final ExecutorService threadPool = Executors.newCachedThreadPool();


    private static void run() throws Exception {

    }

    private static void runSimpleDateFormat(DateFormat df) throws Exception {
        for (int c = 0; c < 3; c++) {
            threadPool.submit(new Callable<Void>() {
                @Override
                public Void call() {
                    Date date;
                    for (int i = 0; i < 3; i++) {
                        try {
                            date = df.parse("2017-12-08 06:02:20");
                            System.out.println(Thread.currentThread().getName() + " : " + date.toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    return null;
                }
            });
        }
    }

    private static void runSyncSimpleDateFormat(SyncSimpleDateFormat syncDF) throws Exception {
        for (int c = 0; c < 3; c++) {
            threadPool.submit(new Callable<Void>() {
                @Override
                public Void call() {
                    Date date;
                    for (int i = 0; i < 3; i++) {
                        try {
                            date = syncDF.parse("2017-12-08 06:02:20");
                            System.out.println(Thread.currentThread().getName() + " : " + date.toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    return null;
                }
            });
        }
    }

    private static void runThreadLocalSimpleDateFormat(ThreadLocal<DateFormat> threadLocalDF) throws Exception {
        for (int c = 0; c < 3; c++) {
            threadPool.submit(new Callable<Void>() {
                @Override
                public Void call() {
                    Date date;
                    for (int i = 0; i < 3; i++) {
                        try {
                            date = threadLocalDF.get().parse("2017-12-08 06:02:20");
                            System.out.println(Thread.currentThread().getName() + " : " + date.toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    return null;
                }
            });
        }
    }

    public static void main(String[] args) throws Exception {

        DateFormat df = new StaticSimpleDateFormat().getDf();
        runSimpleDateFormat(df);
        threadPool.awaitTermination(1, TimeUnit.SECONDS);
        // NOT thread safe! maybe output as:
//        pool-1-thread-3 : Fri Feb 28 18:59:02 CST 2302
//        pool-1-thread-3 : Fri Dec 08 06:02:20 CST 2017
//        pool-1-thread-1 : Fri Dec 08 06:02:20 CST 2017
//        pool-1-thread-2 : Fri Feb 28 18:59:02 CST 2302
//        pool-1-thread-3 : Fri Dec 08 06:02:20 CST 2017
//        pool-1-thread-2 : Fri Dec 08 06:02:20 CST 2017
//        pool-1-thread-2 : Fri Dec 08 06:02:20 CST 2017
//        pool-1-thread-1 : Wed Dec 08 06:02:20 CST 1
//        pool-1-thread-1 : Fri Dec 08 06:02:20 CST 2017
        System.out.println("########\n\n");

        // thread safe, but slower than ThreadLocal
        SyncSimpleDateFormat syncDF = new SyncSimpleDateFormat();
        runSyncSimpleDateFormat(syncDF);
        threadPool.awaitTermination(1, TimeUnit.SECONDS);

        // output is:
//        pool-1-thread-2 : Fri Dec 08 06:02:20 CST 2017
//        pool-1-thread-3 : Fri Dec 08 06:02:20 CST 2017
//        pool-1-thread-1 : Fri Dec 08 06:02:20 CST 2017
//        pool-1-thread-2 : Fri Dec 08 06:02:20 CST 2017
//        pool-1-thread-3 : Fri Dec 08 06:02:20 CST 2017
//        pool-1-thread-1 : Fri Dec 08 06:02:20 CST 2017
//        pool-1-thread-2 : Fri Dec 08 06:02:20 CST 2017
//        pool-1-thread-3 : Fri Dec 08 06:02:20 CST 2017
//        pool-1-thread-1 : Fri Dec 08 06:02:20 CST 2017
        System.out.println("########\n\n");

        // thread safe
        ThreadLocal<DateFormat> threadLocalDF = ThreadLocalSimpleDateFormat.getDf();
        runThreadLocalSimpleDateFormat(threadLocalDF);
        threadPool.awaitTermination(1, TimeUnit.SECONDS);
        threadPool.shutdownNow();

        // output is:
//        pool-1-thread-2 : Fri Dec 08 06:02:20 CST 2017
//        pool-1-thread-3 : Fri Dec 08 06:02:20 CST 2017
//        pool-1-thread-1 : Fri Dec 08 06:02:20 CST 2017
//        pool-1-thread-2 : Fri Dec 08 06:02:20 CST 2017
//        pool-1-thread-3 : Fri Dec 08 06:02:20 CST 2017
//        pool-1-thread-1 : Fri Dec 08 06:02:20 CST 2017
//        pool-1-thread-2 : Fri Dec 08 06:02:20 CST 2017
//        pool-1-thread-3 : Fri Dec 08 06:02:20 CST 2017
//        pool-1-thread-1 : Fri Dec 08 06:02:20 CST 2017

    }
}
