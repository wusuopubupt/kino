package com.mathandcs.kino.abacus.api.partition;

import java.util.Random;

public class ShufflePartitioner<T> extends StreamPartitioner<T> {
    private static final long serialVersionUID = 1L;

    private Random random = new Random();

    private final int[] returnArray = new int[1];

    @Override
    public int[] selectChannels(T record) {
        returnArray[0] = random.nextInt();
        return returnArray;
    }

    @Override
    public StreamPartitioner<T> copy() {
        return new ShufflePartitioner<T>();
    }

    @Override
    public String toString() {
        return "SHUFFLE";
    }
}
