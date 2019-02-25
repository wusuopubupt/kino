package com.mathandcs.kino.effectivejava.highavailability.leaderretrieval;

import com.google.common.base.Preconditions;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * {@link LeaderRetrievalService} implementation which directly forwards calls of
 * notifyListener to the listener.
 */
public class SettableLeaderRetrievalService implements LeaderRetrievalService {

	private String leaderAddress;
	private UUID leaderSessionID;

	private LeaderRetrievalListener listener;

	public SettableLeaderRetrievalService() {
		this(null, null);
	}

	public SettableLeaderRetrievalService(
			@Nullable String leaderAddress,
			@Nullable UUID leaderSessionID) {
		this.leaderAddress = leaderAddress;
		this.leaderSessionID = leaderSessionID;
	}

	@Override
	public synchronized void start(LeaderRetrievalListener listener) throws Exception {
		this.listener = Preconditions.checkNotNull(listener);

		if (leaderSessionID != null && leaderAddress != null) {
			listener.notifyLeaderAddress(leaderAddress, leaderSessionID);
		}
	}

	@Override
	public void stop() throws Exception {

	}

	public synchronized void notifyListener(
			@Nullable String address,
			@Nullable UUID leaderSessionID) {
		this.leaderAddress = address;
		this.leaderSessionID = leaderSessionID;

		if (listener != null) {
			listener.notifyLeaderAddress(address, leaderSessionID);
		}
	}
}
