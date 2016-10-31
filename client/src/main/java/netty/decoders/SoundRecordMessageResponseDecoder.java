package netty.decoders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import recording.SoundRecordMessageResponse;

import java.nio.ByteBuffer;
import java.util.List;

/**
 * Created by kinder112 on 31.10.2016.
 */
public class SoundRecordMessageResponseDecoder extends ByteToMessageDecoder {

    public static final int RESPONSE_SIZE = 10;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < RESPONSE_SIZE) {
            return;
        }

        byte[] buffer = new byte[RESPONSE_SIZE];
        in.readBytes(buffer, 0, RESPONSE_SIZE);
        ByteBuffer.wrap(buffer);

        final SoundRecordMessageResponse response = new SoundRecordMessageResponse(buffer);
        out.add(response);
    }
}
