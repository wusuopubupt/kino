package com.mathandcs.kino.abacus.streaming.runtime.io.rpc.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class NettyConfig {

    private static final Logger LOG = LoggerFactory.getLogger(NettyConfig.class);

    enum TransportType {
        NIO, EPOLL
    }

    static final String SERVER_THREAD_GROUP_NAME = "Kino Netty Server";

    static final String CLIENT_THREAD_GROUP_NAME = "Kino Netty Client";

    private static final String NUM_THREADS_SERVER = "netty.server.threads.num";

    private static final String NUM_THREADS_CLIENT = "netry.client.threads.num";

    private static final String TRANSPORT = "netty.transport";

    private final InetAddress serverAddress;

    private final int serverPort;

    private final Map<String, Object> config;

    public NettyConfig(
            InetAddress serverAddress,
            int serverPort,
            Map<String, Object> config) {

        this.serverAddress = checkNotNull(serverAddress);

        checkArgument(serverPort >= 0 && serverPort <= 65536, "Invalid port number.");
        this.serverPort = serverPort;

        this.config = checkNotNull(config);

        LOG.info(this.toString());
    }

    public InetAddress getServerAddress() {
        return serverAddress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public int getServerNumThreads() {
        return (Integer) config.get(NUM_THREADS_SERVER);
    }

    public int getClientNumThreads() {
        return (Integer) config.get(NUM_THREADS_CLIENT);
    }

    public TransportType getTransportType() {
        return (TransportType) config.get(TRANSPORT);
    }
}
