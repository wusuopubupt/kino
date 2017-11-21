package com.mathandcs.kino.agile.ChainOfResponsibility;

/**
 * Created by dashwang on 11/21/17.
 */
public class TestEntrance {
    public static void main(String[] args) {
        ProjectHandler projectHandler = new ProjectHandler();
        DeptHandler deptHandler = new DeptHandler();
        GeneralHandler generalHandler = new GeneralHandler();
        projectHandler.setNextHandler(deptHandler);
        deptHandler.setNextHandler(generalHandler);

        projectHandler.handle("lwx", 450);
        System.out.println();

        projectHandler.handle("lwx", 600);
        System.out.println();

        projectHandler.handle("zy", 600);
        System.out.println();

        projectHandler.handle("zy", 1500);
        System.out.println();

        projectHandler.handle("lwxzy", 1500);
    }
}
