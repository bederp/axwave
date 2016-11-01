package netty.encoders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import messages.SoundRecordMessage;


/**
 * Encodes {@link recording.SoundRecord}
 */
public class SoundRecordMessageEncoder extends MessageToByteEncoder<SoundRecordMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, SoundRecordMessage msg, ByteBuf out) throws Exception {
        out.writeBytes(msg.toByteArray());
    }
}
