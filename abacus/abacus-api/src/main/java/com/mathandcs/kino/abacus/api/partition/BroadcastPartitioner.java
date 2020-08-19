package com.mathandcs.kino.abacus.api.partition;

/**
 * Partitioner that selects all the output channels.
 */
public class BroadcastPartitioner<T> extends StreamPartitioner<T> {
	private static final long serialVersionUID = 1L;

	int[] returnArray;
	boolean set;
	int setNumber;

	@Override
	public int[] selectChannels(T record) {
		if (set && setNumber == numberOfChannels) {
			return returnArray;
		} else {
			this.returnArray = new int[numberOfChannels];
			for (int i = 0; i < numberOfChannels; i++) {
				returnArray[i] = i;
			}
			set = true;
			setNumber = numberOfChannels;
			return returnArray;
		}
	}

	@Override
	public StreamPartitioner<T> copy() {
		return this;
	}

	@Override
	public String toString() {
		return "BROADCAST";
	}
}
