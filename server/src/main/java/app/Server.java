package app;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import netty.handlers.ServerChannelInitializer;

public class Server {

    private static final int LISTEN_PORT = 19000;

    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = getServerBootstrap(boosGroup, workerGroup);

        bootstrap.bind(LISTEN_PORT).sync();
        System.out.printf("Server started on port %s\n", LISTEN_PORT);
    }

    private static ServerBootstrap getServerBootstrap(NioEventLoopGroup boosGroup, NioEventLoopGroup workerGroup) {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boosGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ServerChannelInitializer());
        return bootstrap;
    }
}
