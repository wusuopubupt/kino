
package com.mathandcs.kino.abacus.streaming.api.timer;


/**
 * Interface for working with time and timers.
 */
public interface TimerService {

	/** Error string for {@link UnsupportedOperationException} on registering timers. */
	String UNSUPPORTED_REGISTER_TIMER_MSG = "Setting timers is only supported on a keyed streams.";

	/** Error string for {@link UnsupportedOperationException} on deleting timers. */
	String UNSUPPORTED_DELETE_TIMER_MSG = "Deleting timers is only supported on a keyed streams.";

	/** Returns the current processing time. */
	long currentProcessingTime();

	/** Returns the current event-time watermark. */
	long currentWatermark();

	/**
	 * Registers a timer to be fired when processing time passes the given time.
	 *
	 * <p>Timers can internally be scoped to keys and/or windows. When you set a timer
	 * in a keyed context, such as in an operation on
	 * {KeyedStream} then that context
	 * will also be active when you receive the timer notification.
	 */
	void registerProcessingTimeTimer(long time);

	/**
	 * Registers a timer to be fired when the event time watermark passes the given time.
	 *
	 * <p>Timers can internally be scoped to keys and/or windows. When you set a timer
	 * in a keyed context, such as in an operation on
	 * {KeyedStream} then that context
	 * will also be active when you receive the timer notification.
	 */
	void registerEventTimeTimer(long time);

	/**
	 * Deletes the processing-time timer with the given trigger time. This method has only an effect if such a timer
	 * was previously registered and did not already expire.
	 *
	 * <p>Timers can internally be scoped to keys and/or windows. When you delete a timer,
	 * it is removed from the current keyed context.
	 */
	void deleteProcessingTimeTimer(long time);

	/**
	 * Deletes the event-time timer with the given trigger time. This method has only an effect if such a timer
	 * was previously registered and did not already expire.
	 *
	 * <p>Timers can internally be scoped to keys and/or windows. When you delete a timer,
	 * it is removed from the current keyed context.
	 */
	void deleteEventTimeTimer(long time);
}
