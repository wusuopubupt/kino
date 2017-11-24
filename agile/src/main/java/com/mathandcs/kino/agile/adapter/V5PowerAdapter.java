package com.mathandcs.kino.agile.adapter;

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
        return 0;
    }
}
