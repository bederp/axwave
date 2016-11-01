package messages;

import java.nio.ByteBuffer;
import java.util.Date;

public class SoundRecordResponseMessage {
    private short magic;
    private long timestamp;

    public SoundRecordResponseMessage(SoundRecordMessage message) {
        magic = message.getMagic();
        timestamp = message.getTimestamp();
    }

    public SoundRecordResponseMessage(byte[] buffer) {
        final ByteBuffer wrappedBuffer = ByteBuffer.wrap(buffer);
        magic = wrappedBuffer.getShort();
        timestamp = wrappedBuffer.getLong();
    }

    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.putShort(magic);
        buffer.putLong(timestamp);
        return buffer.array();
    }

    @Override
    public String toString() {
        return String.format("SoundRecordResponseMessage [Magic: 0x%04X Timestamp %s]", magic, new Date(timestamp));
    }
}
