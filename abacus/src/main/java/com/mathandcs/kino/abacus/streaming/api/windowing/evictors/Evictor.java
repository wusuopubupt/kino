package com.mathandcs.kino.abacus.streaming.api.windowing.evictors;

import com.mathandcs.kino.abacus.streaming.api.windowing.windowns.Window;
import com.mathandcs.kino.abacus.streaming.runtime.record.StreamRecord;
import java.io.Serializable;

/**
 * An {@code Evictor} can remove elements from a pane before/after the evaluation of WindowFunction
 * and after the window evaluation gets triggered by a Trigger
 *
 * @param <T> The type of elements that this {@code Evictor} can evict.
 * @param <W> The type of Windows on which this {@code Evictor} can operate.
 */
public interface Evictor<T, W extends Window> extends Serializable {

    /**
     * Optionally evicts elements. Called before windowing function.
     *
     * @param elements The elements currently in the pane.
     * @param size The current number of elements in the pane.
     * @param window The {@link Window}
     * @param evictorContext The context for the Evictor
     */
    void evictBefore(Iterable<StreamRecord<T>> elements, int size, W window, EvictorContext evictorContext);

    /**
     * Optionally evicts elements. Called after windowing function.
     *
     * @param elements The elements currently in the pane.
     * @param size The current number of elements in the pane.
     * @param window The {@link Window}
     * @param evictorContext The context for the Evictor
     */
    void evictAfter(Iterable<StreamRecord<T>> elements, int size, W window, EvictorContext evictorContext);

    interface EvictorContext {
        long getCurrentProcessingTime();
        long getCurrentWatermark();
    }
}

