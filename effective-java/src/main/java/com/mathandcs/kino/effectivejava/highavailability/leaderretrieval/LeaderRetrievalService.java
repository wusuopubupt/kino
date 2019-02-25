package com.mathandcs.kino.effectivejava.highavailability.leaderretrieval;

/**
 * This interface has to be implemented by a service which retrieves the current leader and notifies
 * a listener about it.
 *
 * <p>Prior to using this service it has to be started by calling the start method. The start method
 * also takes the {@link LeaderRetrievalListener} as an argument. The service can only be started
 * once.
 *
 * <p>The service should be stopped by calling the stop method.
 */
public interface LeaderRetrievalService {

	/**
	 * Starts the leader retrieval service with the given listener to listen for new leaders. This
	 * method can only be called once.
	 *
	 * @param listener The leader retrieval listener which will be notified about new leaders.
	 * @throws Exception
	 */
	void start(LeaderRetrievalListener listener) throws Exception;

	/**
	 * Stops the leader retrieval service.
	 *
	 * @throws Exception
	 */
	void stop() throws Exception;
}
