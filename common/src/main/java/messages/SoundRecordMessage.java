package messages;

import recording.SoundRecord;
import recording.impl.SoundRecordImpl;
import soundformats.AudioFormatEnum;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Date;

/**
 * Message object which wraps {@link SoundRecord} <br>
 * Client sends it to Server <br>
 * Has following format: <br>
 * Magic number (2 bytes) 0x12 0x34 <br>
 * Packet size (2 bytes) (sizeof(timestamp) + sizeof(sound format) + samples.length) <br>
 * Timestamp of first sample in payload (8 bytes) <br>
 * Sound format (2 bytes) (AudioFormatEnum ) <br>
 * Sound samples <br>
 */
public class SoundRecordMessage {

    private static final int TIMESTAMP_SIZE = 8;
    private static final int SOUND_FORMAT_SIZE = 2;
    private static final short MAGIC = 0x1234;

    private SoundRecord record;
    private short magic;
    private Short packetSize;

    /**
     * Constructor that check if {@link SoundRecord} or more precise it's underlying samples
     * will fit the {@link #packetSize}
     *
     * @param record see {@link SoundRecord}
     * @throws MessageSizeExceeded when given sound record is too big for message length field {@link #packetSize}
     */
    public SoundRecordMessage(SoundRecord record) throws MessageSizeExceeded {
        this.record = record;
        this.magic = MAGIC;
        calculatePacketSize();
    }

    /**
     * Private constructor which should be used only when deserializing from byte[]
     *
     * @param record     see {@link SoundRecord}
     * @param magic      2 first bytes when deserializing from byte[]
     * @param packetSize describes remaining length of data in byte[]
     */
    private SoundRecordMessage(SoundRecord record, short magic, short packetSize) {
        this.record = record;
        this.magic = magic;
        this.packetSize = packetSize;
    }

    public byte[] toByteArray() {
        final byte[] magic = shortToBytes(this.magic);
        final byte[] packetSize = shortToBytes(this.packetSize);
        final byte[] timestamp = longToBytes(record.getTimestamp());
        final byte[] soundFormat = shortToBytes(record.getAudioFormat().getFormatEncoding());
        final byte[] samples = record.getSamples();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bos.write(magic, 0, magic.length);
        bos.write(packetSize, 0, packetSize.length);
        bos.write(timestamp, 0, timestamp.length);
        bos.write(soundFormat, 0, soundFormat.length);
        bos.write(samples, 0, samples.length);

        return bos.toByteArray();
    }

    public static SoundRecordMessage fromByteArray(byte[] bytes) {
        final ByteBuffer buffer = ByteBuffer.wrap(bytes);

        final short magic = buffer.getShort();
        final short packetSize = buffer.getShort();
        final long timestamp = buffer.getLong();
        final short formatEncoding = buffer.getShort();
        final int samplesSize = buffer.remaining();
        byte[] samples = new byte[samplesSize];
        buffer.get(samples);

        final SoundRecord record = new SoundRecordImpl(AudioFormatEnum.findByFormatEncoding(formatEncoding), samples, timestamp);
        return new SoundRecordMessage(record, magic, packetSize);
    }

    /**
     * Check whether {@link SoundRecord} will fit into the message {@link #packetSize} field
     *
     * @return packetSize according to specified format
     * @throws MessageSizeExceeded when {@link SoundRecord} is to big for this {@link SoundRecordMessage}
     */
    private short calculatePacketSize() throws MessageSizeExceeded {
        if (packetSize == null) {
            final int constSize = TIMESTAMP_SIZE + SOUND_FORMAT_SIZE;
            final int packetSize;
            try {
                packetSize = Math.addExact(constSize, record.getSamples().length);
            } catch (ArithmeticException e) {
                throw new MessageSizeExceeded("Sound record was to big to fit length field");
            }
            if (packetSize > Short.MAX_VALUE) {
                throw new MessageSizeExceeded("Sound record was to big to fit length field");
            }
            this.packetSize = (short) packetSize;
        }
        return packetSize;
    }

    short getMagic() {
        return magic;
    }

    long getTimestamp() {
        return record.getTimestamp();
    }

    public SoundRecord getRecord() {
        return record;
    }

    @Override
    public String toString() {
        return String.format("SoundRecordMessage [Magic: 0x%04X Packet size: %d Timestamp: %s]", magic, packetSize, new Date(getTimestamp()));
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
}
