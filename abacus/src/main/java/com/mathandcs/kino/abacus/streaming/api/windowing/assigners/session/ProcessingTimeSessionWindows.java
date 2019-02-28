package com.mathandcs.kino.abacus.streaming.api.windowing.assigners.session;

import com.mathandcs.kino.abacus.streaming.api.windowing.assigners.MergingWindowAssigner;
import com.mathandcs.kino.abacus.streaming.api.windowing.triggers.ProcessingTimeTrigger;
import com.mathandcs.kino.abacus.streaming.api.windowing.triggers.Trigger;
import com.mathandcs.kino.abacus.streaming.api.windowing.windowns.TimeWindow;
import java.util.Collection;
import java.util.Collections;

public class ProcessingTimeSessionWindows extends MergingWindowAssigner<Object, TimeWindow> {

	protected long sessionTimeout;

	protected ProcessingTimeSessionWindows(long sessionTimeout) {
		if (sessionTimeout <= 0) {
			throw new IllegalArgumentException("ProcessingTimeSessionWindows parameters must satisfy 0 < size");
		}

		this.sessionTimeout = sessionTimeout;
	}

	@Override
	public Collection<TimeWindow> assignWindows(Object element, long timestamp, WindowAssignerContext context) {
		long currentProcessingTime = context.getCurrentProcessingTime();
		return Collections.singletonList(new TimeWindow(currentProcessingTime, currentProcessingTime + sessionTimeout));
	}

	@Override
	public Trigger<Object, TimeWindow> getDefaultTrigger(WindowAssignerContext ctx) {
		return new ProcessingTimeTrigger();
	}

	@Override
	public boolean isEventTime() {
		return false;
	}

	/**
	 * Merge overlapping {@link TimeWindow}s.
	 */
	public void mergeWindows(Collection<TimeWindow> windows, MergeCallback<TimeWindow> c) {
		//
	}

}
