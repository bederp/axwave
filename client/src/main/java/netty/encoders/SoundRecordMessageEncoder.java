package netty.encoders;

import messages.messages.SoundRecordMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import recording.SoundRecord;


/**
 * Encodes SoundRecord to ByteArray adding MAGIC in progress
 */
public class SoundRecordMessageEncoder extends MessageToByteEncoder<SoundRecord> {


    private static final short MAGIC = 0x1234;
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, SoundRecord soundRecord, ByteBuf byteBuf) throws Exception {
        byteBuf.writeBytes(new SoundRecordMessage(soundRecord, MAGIC).toByteStream().toByteArray());
    }
}
