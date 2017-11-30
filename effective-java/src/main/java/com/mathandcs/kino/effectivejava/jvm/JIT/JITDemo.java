package com.mathandcs.kino.effectivejava.jvm.JIT;

/**
 * Created by dashwang on 11/30/17.
 */
public class JITDemo {

    public static void main(String[] args) {

        long tt = 0;

        for (int c = 0; c < 10000; c += 1) {

            long start = System.currentTimeMillis();

            javaFunc(c);

            long end = System.currentTimeMillis();

            tt += (end - start);

        }

        System.out.println("Java: " + (tt / 100));

    }

    private static void javaFunc(int start) {
        double m = start;
        for (int i = 0; i < 100000; i += 1) {
            double x = Math.sin(i * i);
            m += x;
            if (m > 0.5) {
                m += 1;
            }
        }
    }
}
