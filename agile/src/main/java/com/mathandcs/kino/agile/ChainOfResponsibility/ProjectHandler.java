package com.mathandcs.kino.agile.ChainOfResponsibility;

/**
 * Created by dashwang on 11/21/17.
 */
public class ProjectHandler extends Handler {

    @Override
    public void handle(String user, double fee) {
        if(fee < 500) {
            System.out.println("项目经理报销通过: " + fee);
        } else if(getNextHandler() != null) {
            System.out.println("项目经理无权限处理: " + fee);
            getNextHandler().handle(user, fee);
        }
    }

}
