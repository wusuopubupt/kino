package com.mathandcs.kino.agile;

public class Singleton {
    private static Singleton instance = null;
    private Singleton() {}

    public static Singleton Instance() {
        if(instance == null)
            instance = new Singleton();
        return instance;
    }

}