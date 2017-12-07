package com.mathandcs.kino.effectivejava.system;

/**
 * Created by dashwang on 12/7/17.
 *
 * Shutdown gracefully
 */
public class ShutdownHook {
    public static void main(String[] args) {
        System.out.println("program is starting...");

        System.out.println("program is running...");
        try {
            Thread.sleep(1000);
        } catch(InterruptedException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("program is shutting down...");
        Runtime.getRuntime().addShutdownHook(new Thread(){
            // Do some stuff
            @Override
            public void run() {
                System.out.println("Shutting down gracefully!");
            }
        });

        System.out.println("program is has been shut down...");
    }
}
