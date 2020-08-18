package com.mathandcs.kino.effectivejava.agile.adapter;

/**
 * Created by dashwang on 11/24/17.
 * <p>
 * 提供220V电压的电源
 */
public class V220Power {
    public int providePower() {
        System.out.println("Raw input power is: 200v");
        return 220;
    }
}
