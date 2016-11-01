package app;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import netty.decoders.SoundRecordMessageDecoder;
import netty.encoders.SoundRecordMessageResponseEncoder;
import netty.handlers.ServerHandler;

public class Server {

    private static final int MAX_FRAME_LENGTH = 1024 * 1024;
    private static final int LENGTH_FIELD_OFFSET = 2;
    private static final int LENGTH_FIELD_LENGTH = 2;

    private static final int LISTEN_PORT = 19000;

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
                //Decodes frames based on Length field
                pipeline.addLast(new LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH));
                pipeline.addLast(new SoundRecordMessageDecoder());
                pipeline.addLast(new SoundRecordMessageResponseEncoder());
                pipeline.addLast(new ServerHandler());
            }
        });

        bootstrap.bind(LISTEN_PORT).sync();
        System.out.printf("Server started on port %s\n", LISTEN_PORT);
    }
}
