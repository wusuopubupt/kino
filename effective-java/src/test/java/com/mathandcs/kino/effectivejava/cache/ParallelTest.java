package com.mathandcs.kino.effectivejava.cache;

import org.junit.Test;

/**
 * Created by dashwang on 6/12/17.
 *
 * maven并行跑测试
 * ref: http://memorynotfound.com/run-junit-tests-parallel-using-maven/
 */
public class ParallelTest {

    @Test
    public void one() throws InterruptedException {
        Thread.sleep(1000 * 2);
    }

    @Test
    public void two() throws InterruptedException {
        Thread.sleep(1000 * 2);
    }

    @Test
    public void three() throws InterruptedException {
        Thread.sleep(1000 * 2);
    }

    @Test
    public void four() throws InterruptedException {
        Thread.sleep(1000 * 2);
    }

    @Test
    public void five() throws InterruptedException {
        Thread.sleep(1000 * 2);
    }

}