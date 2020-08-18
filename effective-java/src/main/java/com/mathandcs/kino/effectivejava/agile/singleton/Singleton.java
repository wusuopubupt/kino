package com.mathandcs.kino.effectivejava.agile.singleton;

public class Singleton {

    // 私有静态实例,防止被引用
    private static Singleton instance = null;

    // 私有构造方法，防止被实例化
    private Singleton() {}

    public static Singleton Instance() {
        if(instance == null)
            instance = new Singleton();
        return instance;
    }

}