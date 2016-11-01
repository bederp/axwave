package messages;

import recording.SoundRecord;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Date;

/**
 * Formats SoundRecord according to this specification: <br>
 * Magic number (2 bytes) 0x12 0x34 <br>
 * Packet size (2 bytes) (sizeof(timestamp) + sizeof(sound format) + samples.length) <br>
 * Timestamp of first sample in payload (8 bytes) <br>
 * Sound format (2 bytes) (AudioFormatEnum ) <br>
 * Sound samples <br>
 */
public class SoundRecordMessage {

    private static final int TIMESTAMP_SIZE = 8;
    private static final int SOUND_FORMAT_SIZE = 2;
    public static final int MAX_SAMPLES_LENGTH = Short.MAX_VALUE - TIMESTAMP_SIZE - SOUND_FORMAT_SIZE;

    private SoundRecord record;
    private short magic;
    private Short packetSize;

    public SoundRecordMessage(SoundRecord record, short magic) {
        this.record = record;
        this.magic = magic;
        calculatePacketSize();
    }

    public SoundRecordMessage(SoundRecord record, short magic, short packetSize) {
        this(record, magic);
        this.packetSize = packetSize;
    }

    public ByteArrayOutputStream toByteStream() {
        final ByteArrayOutputStream formattedSample = new ByteArrayOutputStream();

        final byte[] magic = shortToBytes(this.magic);
        final byte[] packetSize = shortToBytes(calculatePacketSize());
        final byte[] timestamp = longToBytes(record.getTimestamp());
        final byte[] soundFormat = shortToBytes(record.getAudioFormat().getFormatEncoding());
        final byte[] samples = record.getSamples().toByteArray();

        formattedSample.write(magic, 0, magic.length);
        formattedSample.write(packetSize, 0, packetSize.length);
        formattedSample.write(timestamp, 0, timestamp.length);
        formattedSample.write(soundFormat, 0, soundFormat.length);
        formattedSample.write(samples, 0, samples.length);

        return formattedSample;
    }

    private short calculatePacketSize() throws ArithmeticException {
        if (packetSize == null) {
            final int constSize = TIMESTAMP_SIZE + SOUND_FORMAT_SIZE;
            final int packetSize;
            try {
                packetSize = Math.addExact(constSize, record.getSamples().size());
            } catch (ArithmeticException e) {
                throw new ArithmeticException("Packet size has overflown int");
            }
            if(packetSize > Short.MAX_VALUE){
                throw new ArithmeticException("Packet size has overflown short");
            }
            this.packetSize = (short) packetSize;
        }
        return packetSize;
    }

    private byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

    private byte[] shortToBytes(short x) {
        ByteBuffer buffer = ByteBuffer.allocate(Short.BYTES);
        buffer.putShort(x);
        return buffer.array();
    }

    short getMagic() {
        return magic;
    }

    long getTimestamp() {
        return record.getTimestamp();
    }

    @Override
    public String toString() {
        return String.format("SoundRecordMessage [Magic: 0x%04X Packet size: %d Timestamp: %s]", magic, packetSize, new Date(getTimestamp()));
    }
}
