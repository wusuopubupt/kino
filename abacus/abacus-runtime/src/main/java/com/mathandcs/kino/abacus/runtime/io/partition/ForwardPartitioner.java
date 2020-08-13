package com.mathandcs.kino.abacus.runtime.io.partition;

/**
 * Partitioner that forwards elements only to the locally running downstream operation.
 *
 */
public class ForwardPartitioner<T> extends StreamPartitioner<T> {
	private static final long serialVersionUID = 1L;

	private final int[] returnArray = new int[] {0};

	@Override
	public int[] selectChannels(T record) {
		return returnArray;
	}

	public StreamPartitioner<T> copy() {
		return this;
	}

	@Override
	public String toString() {
		return "FORWARD";
	}
}
