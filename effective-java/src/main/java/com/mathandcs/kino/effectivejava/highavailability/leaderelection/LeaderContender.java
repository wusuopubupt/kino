package com.mathandcs.kino.effectivejava.highavailability.leaderelection;

import java.util.UUID;

/**
 * Interface which has to be implemented to take part in the leader election process of the
 * {@link LeaderElectionService}.
 */
public interface LeaderContender {

	/**
	 * Callback method which is called by the {@link LeaderElectionService} upon selecting this
	 * instance as the new leader. The method is called with the new leader session ID.
	 *
	 * @param leaderSessionID New leader session ID
	 */
	void grantLeadership(UUID leaderSessionID);

	/**
	 * Callback method which is called by the {@link LeaderElectionService} upon revoking the
	 * leadership of a former leader. This might happen in case that multiple contenders have
	 * been granted leadership.
	 */
	void revokeLeadership();

	/**
	 * Returns the address of the {@link LeaderContender} under which other instances can connect
	 * to it.
	 *
	 * @return Address of this contender.
	 */
	String getAddress();

	/**
	 * Callback method which is called by {@link LeaderElectionService} in case of an error in the
	 * service thread.
	 *
	 * @param exception Caught exception
	 */
	void handleError(Exception exception);
}
