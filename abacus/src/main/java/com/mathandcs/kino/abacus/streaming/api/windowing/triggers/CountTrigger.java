
package com.mathandcs.kino.abacus.streaming.api.windowing.triggers;

import com.mathandcs.kino.abacus.streaming.api.windowing.windowns.Window;
import java.util.concurrent.atomic.AtomicLong;

/**
 * A {@link Trigger} that fires once the count of elements in a pane reaches the given count.
 *
 * @param <W> The type of {@link Window Windows} on which this trigger can operate.
 */
public class CountTrigger<W extends Window> extends Trigger<Object, W> {
	private static final long serialVersionUID = 1L;

	private final long maxCount;

	AtomicLong count = new AtomicLong(0);

	private CountTrigger(long maxCount) {
		this.maxCount = maxCount;
	}

	@Override
	public TriggerResult onElement(Object element, long timestamp, W window, TriggerContext ctx) throws Exception {
		count.incrementAndGet();
		if (count.get() >= maxCount) {
			count.set(0);
			return TriggerResult.FIRE;
		}
		return TriggerResult.CONTINUE;
	}

	@Override
	public TriggerResult onEventTime(long time, W window, TriggerContext ctx) {
		return TriggerResult.CONTINUE;
	}

	@Override
	public TriggerResult onProcessingTime(long time, W window, TriggerContext ctx) throws Exception {
		return TriggerResult.CONTINUE;
	}
}
