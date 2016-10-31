package netty.handlers;

import formaters.SoundRecordMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import recording.SoundRecordMessageResponse;

/**
 * Created by kinder112 on 30.10.2016.
 */
public class ServerHandler extends SimpleChannelInboundHandler<SoundRecordMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SoundRecordMessage msg) throws Exception {
        System.out.println("GOT SOUND RECORD!");
        System.out.println("Sending back magic + timestamp");
        ctx.writeAndFlush(new SoundRecordMessageResponse(msg));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
