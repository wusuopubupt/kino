package com.mathandcs.kino.abacus.streaming.api.windowing.assigners.tumbling;

import com.mathandcs.kino.abacus.streaming.api.windowing.assigners.WindowAssigner;
import com.mathandcs.kino.abacus.streaming.api.windowing.triggers.ProcessingTimeTrigger;
import com.mathandcs.kino.abacus.streaming.api.windowing.triggers.Trigger;
import com.mathandcs.kino.abacus.streaming.api.windowing.windowns.TimeWindow;
import java.util.Collection;
import java.util.Collections;

public class TumblingProcessingTimeWindows extends WindowAssigner<Object, TimeWindow> {

    private final long size;

    private final long offset;

    private TumblingProcessingTimeWindows(long size, long offset) {
        if (offset < 0 || offset >= size) {
            throw new IllegalArgumentException("TumblingProcessingTimeWindows parameters must satisfy  0 <= offset < size");
        }

        this.size = size;
        this.offset = offset;
    }

    @Override
    public Collection<TimeWindow> assignWindows(Object element, long timestamp, WindowAssignerContext context) {
        final long now = context.getCurrentProcessingTime();
        long start = TimeWindow.getWindowStartWithOffset(now, offset, size);
        return Collections.singletonList(new TimeWindow(start, start + size));
    }

    public long getSize() {
        return size;
    }

    @Override
    public Trigger<Object, TimeWindow> getDefaultTrigger(WindowAssignerContext ctx) {
        return new ProcessingTimeTrigger();
    }

    @Override
    public boolean isEventTime() {
        return false;
    }
}
