package netty.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import messages.SoundRecordResponseMessage;


/**
 * Handles Incoming {@link SoundRecordResponseMessage}
 * In this case we just print magic and timestamp to console
 */
public class ClientHandler extends SimpleChannelInboundHandler<SoundRecordResponseMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SoundRecordResponseMessage msg) throws Exception {
        System.out.println("GOT: " + msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
