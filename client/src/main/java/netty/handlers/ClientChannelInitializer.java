package netty.handlers;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import netty.decoders.SoundRecordMessageResponseDecoder;
import netty.encoders.SoundRecordMessageEncoder;

/**
 * Initializes Client pipeline with appropriate handlers
 */
public class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        final ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new SoundRecordMessageEncoder());
        pipeline.addLast(new SoundRecordMessageResponseDecoder());
        pipeline.addLast(new SoundRecordResponseMessageHandler());
    }
}
