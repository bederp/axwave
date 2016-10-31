package netty.decoders;

import formaters.SoundRecordMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import recording.SoundRecord;
import recording.impl.SoundRecordImpl;
import soundformats.AudioFormatEnum;

import java.io.ByteArrayOutputStream;


/**
 * Formats SoundRecord according to this specification: <br>
 * Magic number (2 bytes) 0x12 0x34 <br>
 * Packet size (2 bytes) (sizeof(timestamp) + sizeof(sound format) + samples.length) <br>
 * Timestamp of first sample in payload (8 bytes) <br>
 * Sound format (2 bytes) (AudioFormatEnum ) <br>
 * Sound samples <br>
 */

/**
 * Created by kinder112 on 30.10.2016.
 */
public class SoundRecordMessageDecoder extends LengthFieldBasedFrameDecoder {

    public static final int BYTES_TO_PACKET_SIZE = 4;

    public SoundRecordMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf decoded = (ByteBuf) super.decode(ctx, in);

        if (decoded == null) {
            return null;
        }

        final short magic = decoded.readShort();
        final short packetSize = decoded.readShort();
        final long timestamp = decoded.readLong();
        final short formatEncoding = decoded.readShort();
        ByteArrayOutputStream samples = new ByteArrayOutputStream();
        samples.write(decoded.array());

        final SoundRecord record = new SoundRecordImpl(AudioFormatEnum.findByFormatEncoding(formatEncoding), samples, timestamp);

        return new SoundRecordMessage(record, magic);
    }

}
