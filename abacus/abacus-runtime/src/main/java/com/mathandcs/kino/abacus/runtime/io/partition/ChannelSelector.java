package com.mathandcs.kino.abacus.runtime.io.partition;

public interface ChannelSelector<T> {
	void setup(int numChannels);
	int[] selectChannels(T record);
}
