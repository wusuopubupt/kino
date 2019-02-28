
package com.mathandcs.kino.abacus.streaming.api.windowing.assigners.tumbling;

import com.mathandcs.kino.abacus.streaming.api.windowing.assigners.WindowAssigner;
import com.mathandcs.kino.abacus.streaming.api.windowing.triggers.EventTimeTrigger;
import com.mathandcs.kino.abacus.streaming.api.windowing.triggers.Trigger;
import com.mathandcs.kino.abacus.streaming.api.windowing.windowns.TimeWindow;
import java.util.Collection;
import java.util.Collections;

public class TumblingEventTimeWindows extends WindowAssigner<Object, TimeWindow> {

    private final long size;

    private final long offset;

    protected TumblingEventTimeWindows(long size, long offset) {
        if (offset < 0 || offset >= size) {
            throw new IllegalArgumentException("TumblingEventTimeWindows parameters must satisfy 0 <= offset < size");
        }

        this.size = size;
        this.offset = offset;
    }

    @Override
    public Collection<TimeWindow> assignWindows(Object element, long timestamp, WindowAssignerContext context) {
        long start = TimeWindow.getWindowStartWithOffset(timestamp, offset, size);
        return Collections.singletonList(new TimeWindow(start, start + size));
    }

    @Override
    public Trigger<Object, TimeWindow> getDefaultTrigger(WindowAssignerContext ctx) {
        return new EventTimeTrigger();
    }

    @Override
    public boolean isEventTime() {
        return true;
    }
}
