package com.mathandcs.kino.abacus.streaming.api.windowing.assigners.session;

import com.mathandcs.kino.abacus.streaming.api.windowing.assigners.MergingWindowAssigner;
import com.mathandcs.kino.abacus.streaming.api.windowing.triggers.EventTimeTrigger;
import com.mathandcs.kino.abacus.streaming.api.windowing.triggers.Trigger;
import com.mathandcs.kino.abacus.streaming.api.windowing.windowns.TimeWindow;
import java.util.Collection;
import java.util.Collections;

public class EventTimeSessionWindows extends MergingWindowAssigner<Object, TimeWindow> {

	protected long sessionTimeout;

	protected EventTimeSessionWindows(long sessionTimeout) {
		if (sessionTimeout <= 0) {
			throw new IllegalArgumentException("EventTimeSessionWindows parameters must satisfy 0 < size");
		}

		this.sessionTimeout = sessionTimeout;
	}

	@Override
	public Collection<TimeWindow> assignWindows(Object element, long timestamp, WindowAssignerContext context) {
		return Collections.singletonList(new TimeWindow(timestamp, timestamp + sessionTimeout));
	}

	@Override
	public Trigger<Object, TimeWindow> getDefaultTrigger(WindowAssignerContext ctx) {
		return new EventTimeTrigger();
	}

	@Override
	public boolean isEventTime() {
		return true;
	}

	@Override
	public void mergeWindows(Collection<TimeWindow> windows, MergeCallback<TimeWindow> callback) {
		// not support.
	}
}
