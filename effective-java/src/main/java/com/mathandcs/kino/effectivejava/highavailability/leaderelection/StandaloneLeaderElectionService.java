package com.mathandcs.kino.effectivejava.highavailability.leaderelection;

import com.google.common.base.Preconditions;
import com.mathandcs.kino.effectivejava.highavailability.HighAvailabilityServices;
import java.util.UUID;
import javax.annotation.Nonnull;

/**
 * Standalone implementation of the {@link LeaderElectionService} interface. The standalone
 * implementation assumes that there is only a single {@link LeaderContender} and thus directly
 * grants him the leadership upon start up. Furthermore, there is no communication needed between
 * multiple standalone leader election services.
 */
public class StandaloneLeaderElectionService implements LeaderElectionService {

	private LeaderContender contender = null;

	@Override
	public void start(LeaderContender newContender) throws Exception {
		if (contender != null) {
			// Service was already started
			throw new IllegalArgumentException("Leader election service cannot be started multiple times.");
		}

		contender = Preconditions.checkNotNull(newContender);

		// directly grant leadership to the given contender
		contender.grantLeadership(HighAvailabilityServices.DEFAULT_LEADER_ID);
	}

	@Override
	public void stop() {
		if (contender != null) {
			contender.revokeLeadership();
			contender = null;
		}
	}

	@Override
	public void confirmLeaderSessionID(UUID leaderSessionID) {}

	@Override
	public boolean hasLeadership(@Nonnull UUID leaderSessionId) {
		return (contender != null && HighAvailabilityServices.DEFAULT_LEADER_ID.equals(leaderSessionId));
	}
}
