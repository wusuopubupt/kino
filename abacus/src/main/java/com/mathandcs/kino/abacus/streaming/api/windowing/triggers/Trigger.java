package com.mathandcs.kino.abacus.streaming.api.windowing.triggers;

import com.mathandcs.kino.abacus.streaming.api.windowing.windowns.Window;
import java.io.Serializable;

/**
 * A {@code Trigger} determines when a pane of a window should be evaluated to emit the
 * results for that part of the window.
 *
 * @param <T> The type of elements on which this {@code Trigger} works.
 * @param <W> The type of {@link Window Windows} on which this {@code Trigger} can operate.
 */
public abstract class Trigger<T, W extends Window> implements Serializable {

	private static final long serialVersionUID = -4104633972991191369L;

	/**
	 * Called for every element that gets added to a pane. The result of this will determine
	 * whether the pane is evaluated to emit results.
	 *
	 * @param element The element that arrived.
	 * @param timestamp The timestamp of the element that arrived.
	 * @param window The window to which the element is being added.
	 * @param ctx A context object that can be used to register timer callbacks.
	 */
	public abstract TriggerResult onElement(T element, long timestamp, W window, TriggerContext ctx) throws Exception;

	/**
	 * Called when a processing-time timer that was set using the trigger context fires.
	 *
	 * @param time The timestamp at which the timer fired.
	 * @param window The window for which the timer fired.
	 * @param ctx A context object that can be used to register timer callbacks.
	 */
	public abstract TriggerResult onProcessingTime(long time, W window, TriggerContext ctx) throws Exception;

	/**
	 * Called when an event-time timer that was set using the trigger context fires.
	 *
	 * @param time The timestamp at which the timer fired.
	 * @param window The window for which the timer fired.
	 * @param ctx A context object that can be used to register timer callbacks.
	 */
	public abstract TriggerResult onEventTime(long time, W window, TriggerContext ctx) throws Exception;

	public interface TriggerContext {
		long getCurrentProcessingTime();
		long getCurrentWatermark();
	}
}
