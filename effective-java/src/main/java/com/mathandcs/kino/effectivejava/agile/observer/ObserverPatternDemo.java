package com.mathandcs.kino.effectivejava.agile.observer;

/**
 * Created by dashwang on 11/25/17.
 *
 * TicketPollingService是被观察者, 负责轮询车票状态, 当车票就绪时, 通知所有观察者执行onTicketReadyEvent方法
 */
public class ObserverPatternDemo {

    public static void main(String[] args) {
        TicketPollingService ticketPollingService = new TicketPollingService();

        ticketPollingService.registerObserver(new SMSObserver());
        ticketPollingService.registerObserver(new EmailObserver());

        boolean ticketReady = false;
        while (!ticketReady) {
            ticketReady = ticketPollingService.pollTicketStatus();
        }
        ticketPollingService.notifyAllObservers();
    }
}
