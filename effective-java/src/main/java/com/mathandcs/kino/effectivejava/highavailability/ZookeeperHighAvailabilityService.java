package com.mathandcs.kino.effectivejava.highavailability;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

import com.mathandcs.kino.effectivejava.highavailability.leaderelection.LeaderElectionService;
import com.mathandcs.kino.effectivejava.highavailability.leaderelection.ZooKeeperLeaderElectionService;
import com.mathandcs.kino.effectivejava.highavailability.leaderretrieval.LeaderRetrievalService;
import com.mathandcs.kino.effectivejava.highavailability.leaderretrieval.ZooKeeperLeaderRetrievalService;
import java.util.concurrent.Executor;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.DefaultACLProvider;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of the {@link HighAvailabilityServices} using Apache ZooKeeper.
 * The services store data in ZooKeeper's nodes as illustrated by teh following tree structure:
 *
 * <pre>
 * /kino
 *      +/cluster_id_1/job-id-1/job_manager_lock
 *      |            +/job-id-2/job_manager_lock
 *      |
 *      +/cluster_id_2/job-id-1/job_manager_lock
 *
 * </pre>
 *
 * <p>The root path "/kino" is ZK root path.
 * This makes sure current system stores its data under specific subtrees in ZooKeeper, for example to
 * accommodate specific permission.
 *
 * <p>The "cluster_id" part identifies the data stored for a specific current system "cluster".
 * This "cluster" can be either a standalone or containerized current system cluster, or it can be job
 * on a framework like YARN or Mesos (in a "per-job-cluster" mode).
 *
 */
public class ZookeeperHighAvailabilityService implements HighAvailabilityServices {

    private static final Logger LOG = LoggerFactory.getLogger(
            ZookeeperHighAvailabilityService.class);

    private static final String JOB_MANAGER_LEADER_PATH = "/job_manager_lock";

    /** The ZooKeeper client to use */
    private CuratorFramework client;

    /** ZK config */
    private String zkQuorum;
    private String root;
    private String namespace;
    private String rootWithNamespace;
    private String latchPath;
    private String leaderPath;

    public ZookeeperHighAvailabilityService(
            CuratorFramework client,
            Executor executor,
            String zkQuorum,
            String root,
            String namespace,
            String latchPath,
            String leaderPath) {
        this.client = checkNotNull(client);
        this.zkQuorum = checkNotNull(zkQuorum);
        this.root = checkNotNull(root);
        this.namespace = checkNotNull(namespace);
        this.rootWithNamespace = generateZookeeperPath(root, namespace);
        this.latchPath = checkNotNull(latchPath);
        this.leaderPath = checkNotNull(leaderPath);
        this.client = startCuratorFramework();
    }

    /**
     * Starts a {@link CuratorFramework} instance and connects it to the given ZooKeeper
     * quorum.
     *
     * @return {@link CuratorFramework} instance
     */
    public CuratorFramework startCuratorFramework() {
        LOG.info("Using '{}' as Zookeeper namespace.", rootWithNamespace);

        CuratorFramework cf = CuratorFrameworkFactory.builder()
                .connectString(zkQuorum)
                .retryPolicy(new ExponentialBackoffRetry(2000, 10))
                .namespace(rootWithNamespace.startsWith("/") ? rootWithNamespace.substring(1) : rootWithNamespace)
                .aclProvider(new DefaultACLProvider())
                .build();

        cf.start();

        return cf;
    }

    private static String generateZookeeperPath(String root, String namespace) {
        return root + namespace;
    }

    @Override
    public LeaderRetrievalService getJobManagerLeaderRetriever(Long jobID) {
        String pathSuffix = getPathForJobManager(jobID);
        String leaderPath = rootWithNamespace + pathSuffix;
        return new ZooKeeperLeaderRetrievalService(client, leaderPath);
    }

    @Override
    public LeaderElectionService getJobManagerLeaderElectionService(Long jobID) {
        String pathSuffix = getPathForJobManager(jobID);
        String latchPathWithSuffix = latchPath + pathSuffix;
        String leaderPathWithSuffix = leaderPath + pathSuffix;
        return new ZooKeeperLeaderElectionService(client, latchPathWithSuffix, leaderPathWithSuffix);
    }

    @Override
    public void close() throws Exception {
        if (null != client) {
            client.close();
        }
    }

    private static String getPathForJobManager(final Long jobID) {
        return "/" + jobID + JOB_MANAGER_LEADER_PATH;
    }
}
