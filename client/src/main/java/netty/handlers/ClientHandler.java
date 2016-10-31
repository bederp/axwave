package netty.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import recording.SoundRecordMessageResponse;

/**
 * Created by kinder112 on 30.10.2016.
 */
public class ClientHandler extends SimpleChannelInboundHandler<SoundRecordMessageResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SoundRecordMessageResponse msg) throws Exception {
        System.out.printf("GOT RESPONSE");
        System.out.println(msg.getTimestamp());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
