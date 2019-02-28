package com.mathandcs.kino.abacus.streaming.api.windowing.triggers;

/**
 * Result type for trigger methods. This determines what happens with the window,
 * for example whether the window function should be called, or the window
 * should be discarded.
 *
 * <p>If a {@link Trigger} returns {@link #FIRE} or {@link #FIRE_AND_PURGE} but the window does not
 * contain any data the window function will not be invoked, i.e. no data will be produced for the
 * window.
 */
public enum TriggerResult {

	/**
	 * No action is taken on the window.
	 */
	CONTINUE(false, false),

	/**
	 * {@code FIRE_AND_PURGE} evaluates the window function and emits the window
	 * result.
	 */
	FIRE_AND_PURGE(true, true),

	/**
	 * On {@code FIRE}, the window is evaluated and results are emitted.
	 * The window is not purged, though, all elements are retained.
	 */
	FIRE(true, false),

	/**
	 * All elements in the window are cleared and the window is discarded,
	 * without evaluating the window function or emitting any elements.
	 */
	PURGE(false, true);

	// ------------------------------------------------------------------------

	private final boolean fire;
	private final boolean purge;

	TriggerResult(boolean fire, boolean purge) {
		this.purge = purge;
		this.fire = fire;
	}

	public boolean isFire() {
		return fire;
	}

	public boolean isPurge() {
		return purge;
	}
}
