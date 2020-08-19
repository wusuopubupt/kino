package com.mathandcs.kino.abacus.api.partition;

/**
 * Partitioner that sends all elements to the downstream operator with subtask ID=0.
 */
public class GlobalPartitioner<T> extends StreamPartitioner<T> {
	private static final long serialVersionUID = 1L;

	private final int[] returnArray = new int[] { 0 };

	@Override
	public int[] selectChannels(T record) {
		return returnArray;
	}

	@Override
	public StreamPartitioner<T> copy() {
		return this;
	}

	@Override
	public String toString() {
		return "GLOBAL";
	}
}
