package com.mathandcs.kino.effectivejava.agile.adapter;

/**
 * Created by wangdongxu on 11/24/17.
 */
public class V5PowerAdapter implements V5Power {

    private V220Power v220Power;

    // 组合
    public V5PowerAdapter(V220Power v220Power) {
        this.v220Power = v220Power;
    }

    @Override
    public int providePower() {
        // 1. get 220v power
        int power = v220Power.providePower();

        // 2. transfer 220v power to 5v power
        if(power > 0) {
            System.out.println("Transfer 220v power to 5v power by adapter!");
        }

        return 5;
    }
}
