package app;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import netty.encoders.SoundRecordMessageResponseEncoder;
import netty.handlers.ServerHandler;

/**
 * Created by kinder112 on 30.10.2016.
 */
public class Server {

    public static final int MAX_FRAME_LENGTH = 1024 * 1024;
    public static final int LENGTH_FIELD_OFFSET = 2;
    public static final int LENGTH_FIELD_LENGTH = 2;

    public static final int LISTEN_PORT = 19000;

    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup boosGroup = new NioEventLoopGroup();
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boosGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);

        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH));
                pipeline.addLast(new SoundRecordMessageResponseEncoder());
                pipeline.addLast(new ServerHandler());
            }
        });

        bootstrap.bind(LISTEN_PORT).sync();
        System.out.printf("Server started on port %s", LISTEN_PORT);
    }
}
