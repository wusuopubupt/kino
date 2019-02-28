package com.mathandcs.kino.abacus.streaming.api.windowing.evictors;

import com.mathandcs.kino.abacus.streaming.api.windowing.time.Time;
import com.mathandcs.kino.abacus.streaming.api.windowing.windowns.Window;
import com.mathandcs.kino.abacus.streaming.runtime.record.StreamRecord;
import java.util.Iterator;

/**
 * An {@link Evictor} that keeps elements for a certain amount of time. Elements older
 * than {@code current_time - keep_time} are evicted. The current_time is time associated
 * with StreamRecord
 *
 * @param <W> The type of {@link Window Windows} on which this {@code Evictor} can operate.
 */
public class TimeEvictor<T, W extends Window> implements Evictor<T, W> {

	private final long windowSize;
	private final boolean doEvictAfter;

	public TimeEvictor(long windowSize) {
		this.windowSize = windowSize;
		this.doEvictAfter = false;
	}

	public TimeEvictor(long windowSize, boolean doEvictAfter) {
		this.windowSize = windowSize;
		this.doEvictAfter = doEvictAfter;
	}

	@Override
	public void evictBefore(Iterable<StreamRecord<T>> elements, int size, W window, EvictorContext ctx) {
		if (!doEvictAfter) {
			evict(elements, size, ctx);
		}
	}

	@Override
	public void evictAfter(Iterable<StreamRecord<T>> elements, int size, W window, EvictorContext ctx) {
		if (doEvictAfter) {
			evict(elements, size, ctx);
		}
	}

	private void evict(Iterable<StreamRecord<T>> elements, int size, EvictorContext ctx) {
		if (!hasTimestamp(elements)) {
			return;
		}

		long currentTime = getMaxTimestamp(elements);
		long evictCutoff = currentTime - windowSize;

		for (Iterator<StreamRecord<T>> iterator = elements.iterator(); iterator.hasNext(); ) {
			StreamRecord<T> record = iterator.next();
			if (record.getTimestamp() <= evictCutoff) {
				iterator.remove();
			}
		}
	}

	private boolean hasTimestamp(Iterable<StreamRecord<T>> elements) {
		Iterator<StreamRecord<T>> it = elements.iterator();
		if (it.hasNext()) {
			return it.next().isHasTimestamp();
		}
		return false;
	}

	/**
	 * @param elements The elements currently in the pane.
	 * @return The maximum value of timestamp among the elements.
     */
	private long getMaxTimestamp(Iterable<StreamRecord<T>> elements) {
		long currentTime = Long.MIN_VALUE;
		for (Iterator<StreamRecord<T>> iterator = elements.iterator(); iterator.hasNext();){
			StreamRecord<T> record = iterator.next();
			currentTime = Math.max(currentTime, record.getTimestamp());
		}
		return currentTime;
	}

	@Override
	public String toString() {
		return "TimeEvictor(" + windowSize + ")";
	}

	public long getWindowSize() {
		return windowSize;
	}

	/**
	 * Creates a {@code TimeEvictor} that keeps the given number of elements.
	 * Eviction is done before the window function.
	 *
	 * @param windowSize The amount of time for which to keep elements.
	 */
	public static <T, W extends Window> TimeEvictor<T, W> of(Time windowSize) {
		return new TimeEvictor<>(windowSize.toMilliseconds());
	}

	/**
	 * Creates a {@code TimeEvictor} that keeps the given number of elements.
	 * Eviction is done before/after the window function based on the value of doEvictAfter.
	 *
	 * @param windowSize The amount of time for which to keep elements.
	 * @param doEvictAfter Whether eviction is done after window function.
     */
	public static <T, W extends Window> TimeEvictor<T, W> of(Time windowSize, boolean doEvictAfter) {
		return new TimeEvictor<>(windowSize.toMilliseconds(), doEvictAfter);
	}
}
