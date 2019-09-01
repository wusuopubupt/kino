package com.mathandcs.kino.abacus.streaming.runtime.io.rpc.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.avro.ipc.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

import static com.google.common.base.Preconditions.checkState;

class NettyClient {
	
	private static final Logger LOG = LoggerFactory.getLogger(NettyClient.class);

	private NettyConfig config;

	private Bootstrap bootstrap;

	NettyClient(NettyConfig config) {
		this.config = config;
	}

	void start() {
	    checkState(bootstrap == null, "Netty client has already been initialized.");
		
		final long start = System.nanoTime();

		bootstrap = new Bootstrap();

		// Transport specific configuration
		switch (config.getTransportType()) {
			case NIO:
				initNioBootstrap();
				break;
			case EPOLL:
				initEpollBootstrap();
				break;
		}

		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000);

		final long duration = (System.nanoTime() - start) / 1_100_000;
		LOG.info("Start netty client successfully, cost: {} ms.", duration);
    }

	private void initNioBootstrap() {
		String name = NettyConfig.CLIENT_THREAD_GROUP_NAME 
			+ " (" + config.getServerPort() + ")";
		NioEventLoopGroup nioGroup = new NioEventLoopGroup(
			config.getClientNumThreads(), 
			NettyServer.getNamedThreadFactory(name));
		bootstrap.group(nioGroup).channel(NioSocketChannel.class);
	}

	private void initEpollBootstrap() {
		String name = NettyConfig.CLIENT_THREAD_GROUP_NAME 
			+ "(" + config.getServerPort() + ")";
		EpollEventLoopGroup epollGroup = new EpollEventLoopGroup(
			config.getClientNumThreads(),
			NettyServer.getNamedThreadFactory(name));
		bootstrap.group(epollGroup).channel(EpollSocketChannel.class);
    }

	ChannelFuture connect(final InetSocketAddress serverSocketAddr) {
		bootstrap.handler(new ChannelInitialzer<SocketChannel>(){
			channel.pipeline().addLast(protocal.getClientCahnnelHandles());
		});

		return bootstrap.connect(serverSocketAddr);
	}
}
