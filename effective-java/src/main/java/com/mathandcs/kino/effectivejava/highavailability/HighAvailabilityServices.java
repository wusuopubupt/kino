package com.mathandcs.kino.effectivejava.highavailability;

import com.mathandcs.kino.effectivejava.highavailability.leaderelection.LeaderElectionService;
import com.mathandcs.kino.effectivejava.highavailability.leaderretrieval.LeaderRetrievalService;
import java.util.UUID;

/**
 * HA Service like Apache Flink
 *
 */
public interface HighAvailabilityServices extends AutoCloseable {

	UUID DEFAULT_LEADER_ID = new UUID(0, 0);
	Long DEFAULT_JOB_ID = 0L;

	/**
	 * Gets the leader retriever for the job JobMaster which is responsible for the given job
	 *
	 * @param jobID The identifier of the job.
	 * @return Leader retrieval service to retrieve the job manager for the given job
	 */
	LeaderRetrievalService getJobManagerLeaderRetriever(Long jobID);

	/**
	 * Gets the leader election service for the given job.
	 *
	 * @param jobID The identifier of the job running the election.
	 * @return Leader election service for the job manager leader election
	 */
	LeaderElectionService getJobManagerLeaderElectionService(Long jobID);

	// ------------------------------------------------------------------------
	//  Shutdown and Cleanup
	// ------------------------------------------------------------------------

	/**
	 * Closes the high availability services, releasing all resources.
	 * 
	 * <p>This method <b>does not delete or clean up</b> any data stored in external stores
	 * (file systems, ZooKeeper, etc). Another instance of the high availability
	 * services will be able to recover the job.
	 * 
	 * <p>If an exception occurs during closing services, this method will attempt to
	 * continue closing other services and report exceptions only after all services
	 * have been attempted to be closed.
	 *
	 * @throws Exception Thrown, if an exception occurred while closing these services.
	 */
	@Override
	void close() throws Exception;

}
