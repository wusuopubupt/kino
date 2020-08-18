package com.mathandcs.kino.effectivejava.agile.factoryMethod;

/**
 * Created by dashwang on 11/22/17.
 */
public class SmsSender implements Sender {
    @Override
    public void send() {
        System.out.println("This is SmsSender!");
    }
}
