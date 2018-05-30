/**
 * Alipay.com Inc. Copyright (c) 2004-2018 All Rights Reserved.
 */
package com.mathandcs.kino.effectivejava.jvm;

/**
 * @author dash
 */
public class LargeHeapMemory {

    public final static long OUTOFMEMORY = 200000000000L;

    private String oomString;

    private long length;

    StringBuffer sb = new StringBuffer();

    public LargeHeapMemory(long len) {
        this.length = len;

        long i = 0;
        while (i < len) {
            i++;
            try {
                sb.append("a");
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                break;
            }
        }
        this.oomString = sb.toString();

    }

    public String getOom() {
        return oomString;
    }

    public long getLength() {
        return length;
    }

    public static void main(String[] args) {
        LargeHeapMemory javaHeapTest = new LargeHeapMemory(OUTOFMEMORY);
        System.out.println(javaHeapTest.getOom().length());
    }

}