
package com.mathandcs.kino.abacus.streaming.api.functions;

import com.mathandcs.kino.abacus.streaming.api.collector.Collector;
import com.mathandcs.kino.abacus.streaming.api.timer.TimeDomain;
import com.mathandcs.kino.abacus.streaming.api.timer.TimerService;

/**
 * A function that processes elements of a stream.
 *
 * <p>For every element in the input stream {@link #processElement(Object, Context, Collector)}
 * is invoked. This can produce zero or more elements as output. Implementations can also
 * query the time and set timers through the provided {@link Context}. For firing timers
 * {@link #onTimer(long, OnTimerContext, Collector)} will be invoked. This can again produce
 * zero or more elements as output and register further timers.
 *
 * <p><b>NOTE:</b> Access to keyed state and timers (which are also scoped to a key) is only
 * available if the {@code ProcessFunction} is applied on a {@code KeyedStream}.
 *
 * @param <I> Type of the input elements.
 * @param <O> Type of the output elements.
 */
public abstract class ProcessFunction<I, O> implements Function {

	private static final long serialVersionUID = 1L;

	/**
	 * Process one element from the input stream.
	 *
	 * <p>This function can output zero or more elements using the {@link Collector} parameter
	 * and also update internal state or set timers using the {@link Context} parameter.
	 *
	 * @param value The input value.
	 * @param ctx A {@link Context} that allows querying the timestamp of the element and getting
	 *            a {@link TimerService} for registering timers and querying the time. The
	 *            context is only valid during the invocation of this method, do not store it.
	 * @param out The collector for returning result values.
	 *
	 * @throws Exception This method may throw exceptions. Throwing an exception will cause the operation
	 *                   to fail and may trigger recovery.
	 */
	public abstract void processElement(I value, Context ctx, Collector<O> out) throws Exception;

	/**
	 * Called when a timer set using {@link TimerService} fires.
	 *
	 * @param timestamp The timestamp of the firing timer.
	 * @param ctx An {@link OnTimerContext} that allows querying the timestamp of the firing timer,
	 *            querying the {@link TimeDomain} of the firing timer and getting a
	 *            {@link TimerService} for registering timers and querying the time.
	 *            The context is only valid during the invocation of this method, do not store it.
	 * @param out The collector for returning result values.
	 *
	 * @throws Exception This method may throw exceptions. Throwing an exception will cause the operation
	 *                   to fail and may trigger recovery.
	 */
	public void onTimer(long timestamp, OnTimerContext ctx, Collector<O> out) throws Exception {}

	/**
	 * Information available in an invocation of {@link #processElement(Object, Context, Collector)}
	 * or {@link #onTimer(long, OnTimerContext, Collector)}.
	 */
	public abstract class Context {

		/**
		 * Timestamp of the element currently being processed or timestamp of a firing timer.
		 *
		 * <p>This might be {@code null}, for example if the time characteristic of your program
		 * is set to {ProcessingTime}.
		 */
		public abstract Long timestamp();

		/**
		 * A {@link TimerService} for querying time and registering timers.
		 */
		public abstract TimerService timerService();

		/**
		 * Emits a record
		 *
		 */
		public abstract void output(Collector<O> out);
	}

	/**
	 * Information available in an invocation of {@link #onTimer(long, OnTimerContext, Collector)}.
	 */
	public abstract class OnTimerContext extends Context {
		/**
		 * The {@link TimeDomain} of the firing timer.
		 */
		public abstract TimeDomain timeDomain();
	}

}
