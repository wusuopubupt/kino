package com.mathandcs.kino.effectivejava.agile.observer;

/**
 * Created by dashwang on 11/25/17.
 *
 * 邮件观察者, 收到被观察者车票就绪的通知后,调用onTicketReadyEvent方法发送通知邮件
 */
public class EmailObserver implements TicketObserver{
    @Override
    public void onTicketReadyEvent() {
        System.out.println("Ticket is ready, sending notification Email!");
    }
}
