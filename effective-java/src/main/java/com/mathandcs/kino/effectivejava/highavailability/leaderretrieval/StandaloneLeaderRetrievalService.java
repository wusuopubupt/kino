package com.mathandcs.kino.effectivejava.highavailability.leaderretrieval;


import static com.google.common.base.Preconditions.checkNotNull;

import com.mathandcs.kino.effectivejava.highavailability.HighAvailabilityServices;
import java.util.UUID;

/**
 * Standalone implementation of the {@link LeaderRetrievalService}. This implementation
 * assumes that there is only a single contender for leadership
 * (e.g., a single JobManager or ResourceManager process) and that this process is
 * reachable under a constant address.
 *
 * <p>As soon as this service is started, it immediately notifies the leader listener
 * of the leader contender with the pre-configured address.
 */
public class StandaloneLeaderRetrievalService implements LeaderRetrievalService {

	private final Object startStopLock = new Object();

	/** The fix address of the leader. */
	private final String leaderAddress;

	/** The fix leader ID (leader lock fencing token). */
	private final UUID leaderId;

	/** Flag whether this service is started. */
	private boolean started;

	/**
	 * Creates a StandaloneLeaderRetrievalService with the given leader address.
	 * The leaderId will be null.
	 *
	 * @param leaderAddress The leader's pre-configured address
	 * @deprecated Use {@link #StandaloneLeaderRetrievalService(String, UUID)} instead
	 */
	@Deprecated
	public StandaloneLeaderRetrievalService(String leaderAddress) {
		this.leaderAddress = checkNotNull(leaderAddress);
		this.leaderId = HighAvailabilityServices.DEFAULT_LEADER_ID;
	}

	/**
	 * Creates a StandaloneLeaderRetrievalService with the given leader address.
	 *
	 * @param leaderAddress The leader's pre-configured address
	 * @param leaderId      The constant leaderId.
	 */
	public StandaloneLeaderRetrievalService(String leaderAddress, UUID leaderId) {
		this.leaderAddress = checkNotNull(leaderAddress);
		this.leaderId = checkNotNull(leaderId);
	}

	// ------------------------------------------------------------------------

	@Override
	public void start(LeaderRetrievalListener listener) {
		checkNotNull(listener, "Listener must not be null.");

		synchronized (startStopLock) {
			if(started) {
				return;
			}
			started = true;

			// directly notify the listener, because we already know the leading JobManager's address
			listener.notifyLeaderAddress(leaderAddress, leaderId);
		}
	}

	@Override
	public void stop() {
		synchronized (startStopLock) {
			started = false;
		}
	}
}
