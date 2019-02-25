package com.mathandcs.kino.effectivejava.highavailability.leaderretrieval;

import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Classes which want to be notified about a changing leader by the {@link LeaderRetrievalService}
 * have to implement this interface.
 */
public interface LeaderRetrievalListener {

	/**
	 * This method is called by the {@link LeaderRetrievalService} when a new leader is elected.
	 *
	 * @param leaderAddress The address of the new leader
	 * @param leaderSessionID The new leader session ID
	 */
	void notifyLeaderAddress(@Nullable String leaderAddress, @Nullable UUID leaderSessionID);

	/**
	 * This method is called by the {@link LeaderRetrievalService} in case of an exception. This
	 * assures that the {@link LeaderRetrievalListener} is aware of any problems occurring in the
	 * {@link LeaderRetrievalService} thread.
	 * @param exception
	 */
	void handleError(Exception exception);
}
