package com.mathandcs.kino.effectivejava.jvm;

import java.util.ArrayList;
import java.util.List;

/**
 * VM Args: -Xms20m -Xmx20m -XX:+HeapDumpOnOutOfMemoryError
 *
 * @author dashwang
 * @date 4/3/17.
 */
public class HeapOOM {
    static class OOMObject{}

    public static void main(String[] args) {
        List<OOMObject> list = new ArrayList<>();

        while(true) {
            list.add(new OOMObject());
        }
    }
}

//result:
//
//java.lang.OutOfMemoryError: Java heap space
//        Dumping heap to java_pid65740.hprof ...
//        Heap dump file created [27522693 bytes in 0.209 secs]
//        Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
//        at java.util.Arrays.copyOf(Arrays.java:2245)
//        at java.util.Arrays.copyOf(Arrays.java:2219)
//        at java.util.ArrayList.grow(ArrayList.java:242)
//        at java.util.ArrayList.ensureExplicitCapacity(ArrayList.java:216)
//        at java.util.ArrayList.ensureCapacityInternal(ArrayList.java:208)
//        at java.util.ArrayList.add(ArrayList.java:440)
//        at com.mathandcs.kino.effectivejava.jvm.HeapOOM.main(HeapOOM.java:19)