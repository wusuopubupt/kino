package com.mathandcs.kino.abacus;

public class HelloWorldJava {
    public static void main(String args[]) {
        try {
            execute();
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static void execute() throws Exception {
        String s = String.format("%s:%f", "abc", null);
        System.out.println(s);
    }
}