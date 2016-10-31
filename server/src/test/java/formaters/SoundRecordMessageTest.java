package formaters;

import org.junit.Assert;
import org.junit.Test;
import recording.SoundRecord;
import recording.impl.SoundRecordImpl;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

import static soundformats.AudioFormatEnum.PCM_44100_16_STEREO_LE;

/**
 * Created by kinder112 on 30.10.2016.
 */
public class SoundRecordMessageTest {


    /**
     * Expected format is: <br>
     * Magic number (2 bytes) 0x12 0x34 <br>
     * Packet size (2 bytes) (sizeof(timestamp) + sizeof(sound format) + samples.length) <br>
     * Timestamp of first sample in payload (8 bytes) <br>
     * Sound format (2 bytes) (AudioFormatEnum ) <br>
     * Sound samples <br>
     */
    @Test
    public void shouldFormatAccordingToSpec() throws Exception {
        //Given
        ByteArrayOutputStream samples = new ByteArrayOutputStream();
        byte[] samplesBytes ={0x11, 0x12, 0x13, 0x14};
        short magic = 0x1234;
        samples.write(samplesBytes);
        long timestamp = System.currentTimeMillis();

        SoundRecord record = new SoundRecordImpl(PCM_44100_16_STEREO_LE, samples, timestamp);
        SoundRecordMessage formatter = new SoundRecordMessage(record, magic);

        //Packet size = 8(timestamp) + 2(encoding) + 4(samples) = 14 = 0xE
        byte[] magic_and_packetSize = {0x12, 0x34, 0x00, 0x0E};
        //Packet = 2(magic) + 2(packet size) + 14(actual data)
        final ByteBuffer expected = ByteBuffer.allocate(18);
        expected.put(magic_and_packetSize);
        expected.putLong(timestamp);
        expected.putShort(PCM_44100_16_STEREO_LE.getFormatEncoding());
        expected.put(samples.toByteArray());

        //When
        final ByteArrayOutputStream formattedSound = formatter.toByteStream();

        //Then
        final byte[] actual = formattedSound.toByteArray();
        Assert.assertArrayEquals(expected.array(), actual);
    }
}