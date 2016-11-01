package netty.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.SoundRecordResponseMessage;

public class ClientHandler extends SimpleChannelInboundHandler<SoundRecordResponseMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SoundRecordResponseMessage msg) throws Exception {
        System.out.println("GOT: " + msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
