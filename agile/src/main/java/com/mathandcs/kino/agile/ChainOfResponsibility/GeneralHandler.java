package com.mathandcs.kino.agile.ChainOfResponsibility;

/**
 * Created by dashwang on 11/21/17.
 */
public class GeneralHandler extends Handler {

    @Override
    public void handle(String user, double fee) {
        if(fee >= 1000) {
            System.out.println("总经理报销通过: " + fee);
        } else if(getNextHandler() != null) {
            System.out.println("总经理无权处理: " + fee);
            getNextHandler().handle(user, fee);
        }
    }
    
}
