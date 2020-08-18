package com.mathandcs.kino.effectivejava.agile.adapter;

/**
 * Created by dashwang on 11/24/17.
 */
public class AdapterPatternDemo {
    public static void main(String[] args) {
        V5Power v5Power = new V5PowerAdapter(new V220Power());
        Phone phone = new Phone();
        phone.inputPower(v5Power);
    }
}
