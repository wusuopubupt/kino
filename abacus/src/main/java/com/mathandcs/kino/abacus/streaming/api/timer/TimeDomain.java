package com.mathandcs.kino.abacus.streaming.api.timer;

/**
 * {@code TimeDomain} specifies whether a firing timer is based on event time or processing time.
 */
public enum TimeDomain {

	/**
	 * Time is based on the timestamp of events.
	 */
	EVENT_TIME,

	/**
	 * Time is based on the current processing-time of a machine where processing happens.
	 */
	PROCESSING_TIME
}
