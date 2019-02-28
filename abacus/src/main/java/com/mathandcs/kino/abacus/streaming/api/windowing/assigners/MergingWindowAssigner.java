package com.mathandcs.kino.abacus.streaming.api.windowing.assigners;

import com.mathandcs.kino.abacus.streaming.api.windowing.windowns.Window;
import java.util.Collection;

/**
 * A {@code WindowAssigner} that can merge windows.
 *
 * @param <T> The type of elements that this WindowAssigner can assign windows to.
 * @param <W> The type of {@code Window} that this assigner assigns.
 */
public abstract class MergingWindowAssigner<T, W extends Window> extends WindowAssigner<T, W> {

	/**
	 * Determines which windows (if any) should be merged.
	 *
	 * @param windows The window candidates.
	 * @param callback A callback that can be invoked to signal which windows should be merged.
	 */
	public abstract void mergeWindows(Collection<W> windows, MergeCallback<W> callback);

	/**
	 * Callback to be used in {@link #mergeWindows(Collection, MergeCallback)} for specifying which
	 * windows should be merged.
	 */
	public interface MergeCallback<W> {

		/**
		 * Specifies that the given windows should be merged into the result window.
		 *
		 * @param toBeMerged The list of windows that should be merged into one window.
		 * @param mergeResult The resulting merged window.
		 */
		void merge(Collection<W> toBeMerged, W mergeResult);
	}
}
