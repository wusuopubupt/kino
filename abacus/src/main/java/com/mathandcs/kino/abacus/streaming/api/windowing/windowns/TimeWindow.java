package com.mathandcs.kino.abacus.streaming.api.windowing.windowns;

import com.mathandcs.kino.abacus.util.MathUtils;

/**
 * A {@link Window} that represents a time interval from {@code start} (inclusive) to
 * {@code end} (exclusive).
 */
public class TimeWindow extends Window {

	private final long start;
	private final long end;

	public TimeWindow(long start, long end) {
		this.start = start;
		this.end = end;
	}

	@Override
	public long maxTimestamp() {
		return end - 1;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		TimeWindow window = (TimeWindow) o;

		return end == window.end && start == window.start;
	}

	@Override
	public int hashCode() {
		return MathUtils.longToIntWithBitMixing(start + end);
	}

	@Override
	public String toString() {
		return "TimeWindow{" +
				"start=" + start +
				", end=" + end +
				'}';
	}

	/**
	 * Returns {@code true} if this window intersects the given window.
	 */
	public boolean intersects(TimeWindow other) {
		return this.start <= other.end && this.end >= other.start;
	}

	/**
	 * Returns the minimal window covers both this window and the given window.
	 */
	public TimeWindow cover(TimeWindow other) {
		return new TimeWindow(Math.min(start, other.start), Math.max(end, other.end));
	}

	/**
	 * Method to get the window start for a timestamp.
	 *
	 * @param timestamp epoch millisecond to get the window start.
	 * @param offset The offset which window start would be shifted by.
	 * @param windowSize The size of the generated windows.
	 * @return window start
	 */
	public static long getWindowStartWithOffset(long timestamp, long offset, long windowSize) {
		return timestamp - (timestamp - offset + windowSize) % windowSize;
	}
}
