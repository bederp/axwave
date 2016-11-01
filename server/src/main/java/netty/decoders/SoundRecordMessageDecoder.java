package netty.decoders;

import messages.messages.SoundRecordMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import recording.SoundRecord;
import recording.impl.SoundRecordImpl;
import soundformats.AudioFormatEnum;

import java.io.ByteArrayOutputStream;
import java.util.List;


/**
 * Formats SoundRecord according to this specification: <br>
 * Magic number (2 bytes) 0x12 0x34 <br>
 * Packet size (2 bytes) (sizeof(timestamp) + sizeof(sound format) + samples.length) <br>
 * Timestamp of first sample in payload (8 bytes) <br>
 * Sound format (2 bytes) (AudioFormatEnum ) <br>
 * Sound samples <br>
 */
public class SoundRecordMessageDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

        final short magic = in.readShort();
        final short packetSize = in.readShort();
        final long timestamp = in.readLong();
        final short formatEncoding = in.readShort();
        ByteArrayOutputStream samples = new ByteArrayOutputStream();
        in.readBytes(samples, in.readableBytes());

        final SoundRecord record = new SoundRecordImpl(AudioFormatEnum.findByFormatEncoding(formatEncoding), samples, timestamp);
        final SoundRecordMessage message = new SoundRecordMessage(record, magic, packetSize);
        out.add(message);

    }
}
