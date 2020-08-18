package com.mathandcs.kino.effectivejava.agile.observer;

/**
 * Created by dashwang on 11/25/17.
 *
 * 被观察者接口, 仿照java.util.Observable类实现
 */
public interface TicketObservable {
    // 注册观察者
    public void registerObserver(TicketObserver observer);
    // 移除观察者
    public void removeObserver(TicketObserver observer);
    // 通知所有观察者
    public void notifyAllObservers();
}
