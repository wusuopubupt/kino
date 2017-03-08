package com.mathandcs.kino.agile;

public class HelloWorldJava {
    public static void main(String args[]) {
        try {
            execute();
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static void execute() throws Exception {
        boolean flag = f();
        System.out.println("flag is: " + flag);
    }

    public static boolean f() throws Exception {
        if(1 == 1) {
            throw new Exception("1 not eq 2!");
        }
        return true;
    }

}