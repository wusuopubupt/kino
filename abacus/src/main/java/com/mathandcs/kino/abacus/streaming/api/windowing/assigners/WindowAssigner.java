package com.mathandcs.kino.abacus.streaming.api.windowing.assigners;

import com.mathandcs.kino.abacus.streaming.api.windowing.triggers.Trigger;
import com.mathandcs.kino.abacus.streaming.api.windowing.windowns.Window;
import java.io.Serializable;
import java.util.Collection;

/**
 * A {@code WindowAssigner} assigns zero or more {@link Window Windows} to an element.
 *
 * <p>In a window operation, elements are grouped by their key (if available) and by the windows to
 * which it was assigned. The set of elements with the same key and window is called a pane.
 * When a {@link Trigger} decides that a certain pane should fire the
 * {WindowFunction is applied
 * to produce output elements for that pane.
 *
 * @param <T> The type of elements that this WindowAssigner can assign windows to.
 * @param <W> The type of {@code Window} that this assigner assigns.
 */
public abstract class WindowAssigner<T, W extends Window> implements Serializable {
	/**
	 * Returns a {@code Collection} of windows that should be assigned to the element.
	 *
	 * @param element The element to which windows should be assigned.
	 * @param timestamp The timestamp of the element.
	 * @param context The {@link WindowAssignerContext} in which the assigner operates.
	 */
	public abstract Collection<W> assignWindows(T element, long timestamp, WindowAssignerContext context);

	/**
	 * Returns the default trigger associated with this {@code WindowAssigner}.
	 */
	public abstract Trigger<T, W> getDefaultTrigger(WindowAssignerContext cxt);


	/**
	 * Returns {@code true} if elements are assigned to windows based on event time,
	 * {@code false} otherwise.
	 */
	public abstract boolean isEventTime();


	public abstract static class WindowAssignerContext {

		/**
		 * Returns the current processing time.
		 */
		public abstract long getCurrentProcessingTime();

	}
}
