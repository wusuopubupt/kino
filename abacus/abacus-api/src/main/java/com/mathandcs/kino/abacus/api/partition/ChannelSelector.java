package com.mathandcs.kino.abacus.api.partition;

public interface ChannelSelector<T> {
	void setup(int numChannels);
	int[] selectChannels(T record);
}
