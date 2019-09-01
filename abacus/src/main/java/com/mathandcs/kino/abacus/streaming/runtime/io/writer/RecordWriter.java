package com.mathandcs.kino.abacus.streaming.runtime.io.writer;

import com.mathandcs.kino.abacus.streaming.api.common.UniqueId;
import com.mathandcs.kino.abacus.streaming.runtime.io.channel.ChannelSelector;
import com.mathandcs.kino.abacus.streaming.runtime.io.channel.Producer;
import com.mathandcs.kino.abacus.streaming.runtime.utils.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A record-oriented runtime result writer.
 *
 * @param <T> the type of the record that can be emitted with this record writer
 */
public class RecordWriter<T> {

	private static final Logger LOG = LoggerFactory.getLogger(RecordWriter.class);

	private final ChannelSelector<T> channelSelector;

	private Producer<ByteBuffer> producer;

	private List<UniqueId> outputChannelIds;

	private int numberOfChannels;

	private String stream;

	public RecordWriter(ChannelSelector channelSelector, int numberOfChannels, String stream) {
		this.channelSelector = channelSelector;
		this.numberOfChannels = numberOfChannels;
		this.channelSelector.setup(numberOfChannels);
		this.stream = stream;
	}

	public void emit(T record) {
		emit(record, channelSelector.selectChannel(record));
	}

	public void emit(T record, int channelIndex) {
		ByteBuffer buffer = ByteBuffer.wrap(Serializer.encode(record));
		producer.produce(buffer, outputChannelIds.get(channelIndex));
	}

}
