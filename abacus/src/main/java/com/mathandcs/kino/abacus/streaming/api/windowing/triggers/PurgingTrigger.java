package com.mathandcs.kino.abacus.streaming.api.windowing.triggers;

import com.mathandcs.kino.abacus.streaming.api.windowing.windowns.Window;

/**
 * A trigger that can turn any {@link Trigger} into a purging {@code Trigger}.
 *
 * <p>When the nested trigger fires, this will return a {@code FIRE_AND_PURGE}
 * {@link TriggerResult}.
 *
 * @param <T> The type of elements on which this trigger can operate.
 * @param <W> The type of {@link Window Windows} on which this trigger can operate.
 */
public class PurgingTrigger<T, W extends Window> extends Trigger<T, W> {

	private Trigger<T, W> nestedTrigger;

	private  PurgingTrigger(Trigger<T, W> nestedTrigger) {
		this.nestedTrigger = nestedTrigger;
	}

	@Override
	public TriggerResult onElement(T element, long timestamp, W window, TriggerContext ctx) throws Exception {
		TriggerResult triggerResult = nestedTrigger.onElement(element, timestamp, window, ctx);
		return triggerResult.isFire() ? TriggerResult.FIRE_AND_PURGE : triggerResult;
	}

	@Override
	public TriggerResult onEventTime(long time, W window, TriggerContext ctx) throws Exception {
		TriggerResult triggerResult = nestedTrigger.onEventTime(time, window, ctx);
		return triggerResult.isFire() ? TriggerResult.FIRE_AND_PURGE : triggerResult;
	}

	@Override
	public TriggerResult onProcessingTime(long time, W window, TriggerContext ctx) throws Exception {
		TriggerResult triggerResult = nestedTrigger.onProcessingTime(time, window, ctx);
		return triggerResult.isFire() ? TriggerResult.FIRE_AND_PURGE : triggerResult;
	}
}
