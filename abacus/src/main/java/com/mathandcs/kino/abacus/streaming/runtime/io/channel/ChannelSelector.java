package com.mathandcs.kino.abacus.streaming.runtime.io.channel;

/**
 * The {@link ChannelSelector} determines to which logical channels a record
 * should be written to.
 *
 * @param <T> the type of record which is sent through the attached output gate
 */
public interface ChannelSelector<T> {

	/**
	 * Initializes the channel selector with the number of output channels.
	 *
	 * @param numberOfChannels the total number of output channels which are attached
	 * 		to respective output gate.
	 */
	void setup(int numberOfChannels);

	/**
	 * Returns the logical channel index, to which the given record should be written. It is
	 * illegal to call this method for broadcast channel selectors and this method can remain
	 * not implemented in that case (for example by throwing {@link UnsupportedOperationException}).
	 *
	 * @param record the record to determine the output channels for.
	 * @return an integer number which indicates the index of the output
	 * 		channel through which the record shall be forwarded.
	 */
	int selectChannel(T record);

	/**
	 * Returns whether the channel selector always selects all the output channels.
	 *
	 * @return true if the selector is for broadcast mode.
	 */
	boolean isBroadcast();
}
