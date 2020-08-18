package com.mathandcs.kino.effectivejava.agile.observer;

/**
 * Created by dashwang on 11/25/17.
 *
 * 票务系统观察者接口
 */
public interface TicketObserver {
    // 得到通知后调用的方法
    void onTicketReadyEvent();
}
