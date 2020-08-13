package com.mathandcs.kino.abacus.runtime.io.partition;

import java.io.Serializable;

public abstract class StreamPartitioner<T> implements ChannelSelector<T>, Serializable {
	private static final long serialVersionUID = 1L;

	protected int numberOfChannels;

	public void setup(int numberOfChannels) {
		this.numberOfChannels = numberOfChannels;
	}

	public abstract StreamPartitioner<T> copy();
}
