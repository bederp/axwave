package netty.decoders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import messages.SoundRecordMessage;

import java.io.ByteArrayOutputStream;
import java.util.List;


/**
 * Decodes {@link SoundRecordMessage} <br>
 */
public class SoundRecordMessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        in.readBytes(bos, in.readableBytes());
        SoundRecordMessage message = SoundRecordMessage.fromByteArray(bos.toByteArray());
        out.add(message);
    }
}
