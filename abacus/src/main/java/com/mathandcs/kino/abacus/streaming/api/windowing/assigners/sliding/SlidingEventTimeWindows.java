package com.mathandcs.kino.abacus.streaming.api.windowing.assigners.sliding;

import com.mathandcs.kino.abacus.streaming.api.windowing.assigners.WindowAssigner;
import com.mathandcs.kino.abacus.streaming.api.windowing.triggers.EventTimeTrigger;
import com.mathandcs.kino.abacus.streaming.api.windowing.triggers.Trigger;
import com.mathandcs.kino.abacus.streaming.api.windowing.windowns.TimeWindow;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Getter;

public class SlidingEventTimeWindows extends WindowAssigner<Object, TimeWindow> {

    @Getter
    private final long size;
    @Getter
    private final long slide;
    @Getter
    private final long offset;

    public SlidingEventTimeWindows(long size, long slide, long offset) {
        if (offset < 0 || offset >= slide || size <= 0) {
            throw new IllegalArgumentException("SlidingEventTimeWindow parameters must satisfy 0 <= offset < slide "
                    + "and size > 0");
        }
        this.size = size;
        this.slide = slide;
        this.offset = offset;
    }

    @Override
    public Collection<TimeWindow> assignWindows(Object element, long timestamp, WindowAssignerContext context) {
        List<TimeWindow> windows = new ArrayList<>((int) (size / slide));
        long lastStart = TimeWindow.getWindowStartWithOffset(timestamp, offset, slide);
        for (long start = lastStart; start > timestamp - size; start -= slide) {
            windows.add(new TimeWindow(start, start + size));
        }
        return windows;
    }

    @Override
    public Trigger<Object, TimeWindow> getDefaultTrigger(WindowAssignerContext cxt) {
        return new EventTimeTrigger();
    }

    @Override
    public boolean isEventTime() {
        return true;
    }
}