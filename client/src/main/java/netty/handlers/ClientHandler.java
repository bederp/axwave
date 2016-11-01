package netty.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import recording.SoundRecordMessageResponse;

public class ClientHandler extends SimpleChannelInboundHandler<SoundRecordMessageResponse> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SoundRecordMessageResponse msg) throws Exception {
        System.out.printf("GOT RESPONSE Timestamp %d\n", msg.getTimestamp());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
