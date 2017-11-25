package com.mathandcs.kino.agile.observer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dashwang on 11/25/17.
 *
 * 车票轮询服务, 实现TicketObservable接口
 */
public class TicketPollingService implements TicketObservable{

    private static int count = 0;
    private static final int MAX_POLLING_TIME = 2;

    private List<TicketObserver> observers = new ArrayList<>();

    @Override
    public void registerObserver(TicketObserver observer) {
        this.observers.add(observer);
    }

    @Override
    public void removeObserver(TicketObserver observer) {
        this.observers.remove(observer);
    }

    @Override
    public void notifyAllObservers() {
        for(TicketObserver observer : observers) {
            observer.onTicketReadyEvent();
        }
    }

    public static boolean pollTicketStatus() {
        if(count < MAX_POLLING_TIME) {
            System.out.println("Ticket is not ready!");
            count++;
            return false;
        }
        System.out.println("Ticket is ready!");
        return true;
    }
}
