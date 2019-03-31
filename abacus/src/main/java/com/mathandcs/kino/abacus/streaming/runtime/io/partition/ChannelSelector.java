package com.mathandcs.kino.abacus.streaming.runtime.io.partition;

public interface ChannelSelector<T> {
	void setup(int numChannels);
	int[] selectChannels(T record);
}
