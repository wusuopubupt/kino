package com.mathandcs.kino.agile.observer;

/**
 * Created by dashwang on 11/25/17.
 *
 * 短信观察者, 收到被观察者车票就绪的通知后,调用onTicketReadyEvent方法发送通知短信
 */
public class SMSObserver implements TicketObserver{
    @Override
    public void onTicketReadyEvent() {
        System.out.println("Ticket is ready, sending notification SMS!");
    }
}
