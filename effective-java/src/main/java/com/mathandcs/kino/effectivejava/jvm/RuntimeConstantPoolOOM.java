package com.mathandcs.kino.effectivejava.jvm;

import java.util.ArrayList;
import java.util.List;

/**
 * VM Args: -XX:PermSize=10m -XX:MaxPermSize=10m
 * Before jdk1.7, we would get OOM: PermGen space
 *
 * @author dashwang
 */
public class RuntimeConstantPoolOOM {

    public static void main(String[] args) {
        //使用list保持常量池引用,避免Full GC回收常量池行为
        List<String> list = new ArrayList<>();
        int i = 0;
        while(true) {
            list.add(String.valueOf(i++).intern());
        }
    }

}
