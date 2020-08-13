package com.mathandcs.kino.abacus.runtime.io.partition;

public class RebalancePartitioner<T> extends StreamPartitioner<T> {
	private static final long serialVersionUID = 1L;

	private final int[] returnArray = new int[] {-1};

	@Override
	public int[] selectChannels(T record) {
		int newChannel = ++this.returnArray[0];
		if (newChannel >= numberOfChannels) {
			this.returnArray[0] = 0;
		}
		return this.returnArray;
	}

	public StreamPartitioner<T> copy() {
		return this;
	}

	@Override
	public String toString() {
		return "REBALANCE";
	}
}
