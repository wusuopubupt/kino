package com.mathandcs.kino.abacus.streaming.api.windowing.triggers;

import com.mathandcs.kino.abacus.streaming.api.windowing.windowns.TimeWindow;

/**
 * A {@link Trigger} that fires once the current system time passes the end of the window
 * to which a pane belongs.
 */
public class ProcessingTimeTrigger extends Trigger<Object, TimeWindow> {

	public ProcessingTimeTrigger() {}

	@Override
	public TriggerResult onElement(Object element, long timestamp, TimeWindow window, TriggerContext ctx) {
		return TriggerResult.CONTINUE;
	}

	@Override
	public TriggerResult onEventTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {
		return TriggerResult.CONTINUE;
	}

	@Override
	public TriggerResult onProcessingTime(long time, TimeWindow window, TriggerContext ctx) {
		return TriggerResult.FIRE;
	}

}
