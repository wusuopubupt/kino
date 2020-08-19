package com.mathandcs.kino.abacus.runtime.io.writer;

import com.mathandcs.kino.abacus.runtime.io.channel.ChannelSelector;
import com.mathandcs.kino.abacus.runtime.io.channel.Producer;
import com.mathandcs.kino.abacus.runtime.utils.Serializer;
import java.nio.ByteBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A record-oriented runtime result writer.
 *
 * @param <T> the type of the record that can be emitted with this record writer
 */
public class RecordWriter<T> {

	private static final Logger LOG = LoggerFactory.getLogger(RecordWriter.class);

	private final ChannelSelector<T> channelSelector;

	private Producer<ByteBuffer> producer;

	private int numberOfChannels;

	private String stream;

	public RecordWriter(ChannelSelector channelSelector, int numberOfChannels, String stream) {
		this.channelSelector = channelSelector;
		this.numberOfChannels = numberOfChannels;
		this.channelSelector.setup(numberOfChannels);
		this.stream = stream;
	}

	public void write(T record) {
		write(record, channelSelector.selectChannel(record));
	}

	public void write(T record, int channelIndex) {
		ByteBuffer buffer = ByteBuffer.wrap(Serializer.encode(record));
		// TODO(@fandu)
	}

}
