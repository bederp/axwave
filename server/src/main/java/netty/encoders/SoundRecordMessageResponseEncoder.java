package netty.encoders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import recording.SoundRecordMessageResponse;

/**
 * Created by kinder112 on 30.10.2016.
 */
public class SoundRecordMessageResponseEncoder extends MessageToByteEncoder<SoundRecordMessageResponse> {
    @Override
    protected void encode(ChannelHandlerContext ctx, SoundRecordMessageResponse msg, ByteBuf out) throws Exception {
        out.writeBytes(msg.toByteArray());
    }
}
