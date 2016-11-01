package netty.encoders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import messages.SoundRecordResponseMessage;

/**
 * Encodes {@link SoundRecordResponseMessage}
 */
public class SoundRecordMessageResponseEncoder extends MessageToByteEncoder<SoundRecordResponseMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, SoundRecordResponseMessage msg, ByteBuf out) throws Exception {
        out.writeBytes(msg.toByteArray());
    }
}
