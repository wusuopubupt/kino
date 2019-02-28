package com.mathandcs.kino.abacus.streaming.api.windowing.triggers;

import com.mathandcs.kino.abacus.streaming.api.windowing.windowns.TimeWindow;

/**
 * A {@link Trigger} that fires once the watermark passes the end of the window
 * to which a pane belongs.
 *
 */
public class EventTimeTrigger extends Trigger<Object, TimeWindow> {

    public EventTimeTrigger() {}

    @Override
    public TriggerResult onElement(Object element, long timestamp, TimeWindow window, TriggerContext ctx) throws Exception {
        if (window.maxTimestamp() <= ctx.getCurrentWatermark()) {
            // if the watermark is already past the window fire immediately
            return TriggerResult.FIRE;
        } else {
            return TriggerResult.CONTINUE;
        }
    }

    @Override
    public TriggerResult onEventTime(long time, TimeWindow window, TriggerContext ctx) {
        return time == window.maxTimestamp() ?
                TriggerResult.FIRE :
                TriggerResult.CONTINUE;
    }

    @Override
    public TriggerResult onProcessingTime(long time, TimeWindow window, TriggerContext ctx) throws Exception {
        return TriggerResult.CONTINUE;
    }


}
