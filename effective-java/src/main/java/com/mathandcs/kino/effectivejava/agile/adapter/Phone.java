package com.mathandcs.kino.effectivejava.agile.adapter;

/**
 * Created by dashwang on 11/24/17.
 */
public class Phone {
    // 充电
    public void inputPower(V5Power v5Power) {
        int inputPower = v5Power.providePower();
        System.out.println("Input power is: " + inputPower + "v");
    }
}

