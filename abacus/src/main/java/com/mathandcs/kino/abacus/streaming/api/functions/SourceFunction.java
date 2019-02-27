package com.mathandcs.kino.abacus.streaming.api.functions;

import com.mathandcs.kino.abacus.streaming.runtime.record.Watermark;
import java.io.Serializable;

public interface SourceFunction<T> extends Function, Serializable {

	void run(SourceContext<T> ctx) throws Exception;

	void cancel();

	interface SourceContext<T> {

		/**
		 * Emits one element from the source, without attaching a timestamp.
		 *
		 * @param element The element to emit
		 */
		void collect(T element);

		/**
		 * Emits one element from the source, and attaches the given timestamp.
		 * @param element The element to emit
		 * @param timestamp The timestamp in milliseconds since the Epoch
		 */
		void collectWithTimestamp(T element, long timestamp);

		/**
		 * Emits the given {@link Watermark}. A Watermark of value {@code t} declares that no
		 * elements with a timestamp {@code t' <= t} will occur any more.
		 *
		 * @param mark The Watermark to emit
		 */
		void emitWatermark(Watermark mark);

		/**
		 * This method is called by the system to shut down the context.
		 */
		void close();
	}
}
