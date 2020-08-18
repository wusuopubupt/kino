package com.mathandcs.kino.effectivejava.agile.ChainOfResponsibility;

/**
 * Created by dashwang on 11/21/17.
 */
public class DeptHandler extends Handler {

    @Override
    public void handle(String user, double fee) {
        if(fee < 1000) {
            System.out.println("部门经理报销通过: " + fee);
        } else if(getNextHandler() != null) {
            System.out.println("部门经理无权限处理: " + fee);
            getNextHandler().handle(user, fee);
        }
    }
    
}
