package com.mathandcs.kino.abacus.streaming.runtime.io.writer;

import com.mathandcs.kino.abacus.streaming.runtime.io.channel.ChannelSelector;

/**
 * This is the default implementation of the {@link ChannelSelector} interface. It represents a simple round-robin
 * strategy, i.e. regardless of the record every attached exactly one output channel is selected at a time.

 * @param <T>
 *        the type of record which is sent through the attached output gate
 */
public class RoundRobinChannelSelector<T> implements ChannelSelector<T> {

	/** Stores the index of the channel to send the next record to. */
	private int nextChannelToSendTo = -1;

	private int numberOfChannels;

	@Override
	public void setup(int numberOfChannels) {
		this.numberOfChannels = numberOfChannels;
	}

	@Override
	public int selectChannel(final T record) {
		nextChannelToSendTo = (nextChannelToSendTo + 1) % numberOfChannels;
		return nextChannelToSendTo;
	}

	@Override
	public boolean isBroadcast() {
		return false;
	}
}
