package com.mathandcs.kino.abacus.streaming.api.windowing.evictors;

import com.mathandcs.kino.abacus.streaming.api.windowing.windowns.Window;
import com.mathandcs.kino.abacus.streaming.runtime.record.StreamRecord;
import java.util.Iterator;

/**
 * An {@link Evictor} that keeps up to a certain amount of elements.
 */
public class CountEvictor<T, W extends Window> implements Evictor<T, W> {

	private final long maxCount;
	private final boolean doEvictAfter;

	private CountEvictor(long count, boolean doEvictAfter) {
		this.maxCount = count;
		this.doEvictAfter = doEvictAfter;
	}

	private CountEvictor(long count) {
		this.maxCount = count;
		this.doEvictAfter = false;
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
		if (size <= maxCount) {
			return;
		} else {
			int evictedCount = 0;
			for (Iterator<StreamRecord<T>> iterator = elements.iterator(); iterator.hasNext();){
				iterator.next();
				evictedCount++;
				if (evictedCount > size - maxCount) {
					break;
				} else {
					iterator.remove();
				}
			}
		}
	}

	/**
	 * Creates a {@code CountEvictor} that keeps the given number of elements.
	 * Eviction is done before the window function.
	 *
	 * @param maxCount The number of elements to keep in the pane.
	 */
	public static <T, W extends Window> CountEvictor<T, W> of(long maxCount) {
		return new CountEvictor<>(maxCount);
	}

	/**
	 * Creates a {@code CountEvictor} that keeps the given number of elements in the pane
	 * Eviction is done before/after the window function based on the value of doEvictAfter.
	 *
	 * @param maxCount The number of elements to keep in the pane.
	 * @param doEvictAfter Whether to do eviction after the window function.
     */
	public static <T, W extends Window> CountEvictor<T, W> of(long maxCount, boolean doEvictAfter) {
		return new CountEvictor<>(maxCount, doEvictAfter);
	}
}
