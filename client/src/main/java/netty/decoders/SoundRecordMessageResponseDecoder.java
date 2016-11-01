package netty.decoders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import messages.SoundRecordResponseMessage;

import java.nio.ByteBuffer;
import java.util.List;

public class SoundRecordMessageResponseDecoder extends ByteToMessageDecoder {

    private static final int RESPONSE_SIZE = 10;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < RESPONSE_SIZE) {
            return;
        }

        byte[] buffer = new byte[RESPONSE_SIZE];
        in.readBytes(buffer, 0, RESPONSE_SIZE);
        ByteBuffer.wrap(buffer);

        final SoundRecordResponseMessage response = new SoundRecordResponseMessage(buffer);
        out.add(response);
    }
}
