package netty.handlers;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import netty.decoders.SoundRecordMessageDecoder;
import netty.encoders.SoundRecordMessageResponseEncoder;

/**
 * Initializes Server pipeline with appropriate handlers
 */
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {

    private static final int MAX_FRAME_LENGTH = 1024 * 1024;
    private static final int LENGTH_FIELD_OFFSET = 2;
    private static final int LENGTH_FIELD_LENGTH = 2;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new LengthFieldBasedFrameDecoder(MAX_FRAME_LENGTH, LENGTH_FIELD_OFFSET, LENGTH_FIELD_LENGTH));
        pipeline.addLast(new SoundRecordMessageDecoder());
        pipeline.addLast(new SoundRecordMessageResponseEncoder());
        pipeline.addLast(new ServerHandler());
    }
}
