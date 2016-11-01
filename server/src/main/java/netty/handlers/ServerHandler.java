package netty.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.SoundRecordMessage;
import messages.SoundRecordResponseMessage;

public class ServerHandler extends SimpleChannelInboundHandler<SoundRecordMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SoundRecordMessage msg) throws Exception {
        System.out.println("Got: " + msg);
        final SoundRecordResponseMessage response = new SoundRecordResponseMessage(msg);
        System.out.println("Responding: " + response);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
