package recording.impl;

import recording.SoundRecord;
import recording.SoundSource;
import soundformats.AudioFormatEnum;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

/**
 * {@link SoundSource} implementation which tries to record sound from microphone <br>
 * It is not ThreadSafe and only one instance can record at given time due to underlying Audio Device
 */
@Deprecated
class MicrophoneSoundSource implements SoundSource {

    private static final int SECOND_TO_MILLIS = 1000;
    private AudioFormatEnum format;
    private int seconds;
    private ByteArrayOutputStream out = new ByteArrayOutputStream();
    private long timestamp;
    private int oneSecondSize;


    MicrophoneSoundSource(AudioFormatEnum format) {
        this.format = format;
        oneSecondSize = calculateOneSecondSize();
    }

    @Override
    public AudioFormatEnum getFormat() {
        return format;
    }

    @Override
    public SoundRecord recordSound(int seconds) {
        this.seconds = seconds;

        try (TargetDataLine line = AudioSystem.getTargetDataLine(format.getAudioFormat())) {
            startLineRecording(line);
            timestamp = System.currentTimeMillis();
            readSound(line);

        } catch (LineUnavailableException e) {
            System.out.println(e.getMessage());
        }
        return new SoundRecordImpl(format, out.toByteArray(), timestamp);
    }

    @Override
    public SoundRecord recordSound(int from, int to) {
        final SoundRecord record = recordSound(to);
        final long newTimestamp = timestamp + from * SECOND_TO_MILLIS;
        final byte[] bytes = trimSamples(from, to, record);
        return new SoundRecordImpl(format, bytes, newTimestamp);

    }

    private byte[] trimSamples(int from, int to, SoundRecord record) {
        final int newSize = (to - from) * oneSecondSize;
        final byte[] samples = record.getSamples();
        final ByteBuffer buffer = ByteBuffer.wrap(samples);
        buffer.position(oneSecondSize * from);

        final byte[] bytes = new byte[newSize];
        buffer.get(bytes, 0, newSize);
        return bytes;
    }

    private void startLineRecording(TargetDataLine line) throws LineUnavailableException {
        line.flush();
        line.open(format.getAudioFormat());
        line.start();
    }

    private void readSound(TargetDataLine line) {
        int numBytesRead;
        byte[] data = new byte[line.getBufferSize() / 5];

        while (out.size() < oneSecondSize * seconds) {
            numBytesRead = line.read(data, 0, data.length);
            out.write(data, 0, numBytesRead);
        }
    }

    /**
     * Calculate how much bytes do you need to record N seconds in given format
     * @return number of bytes required to store N seconds in desired format
     */
    private int calculateOneSecondSize() {
        AudioFormat audioFormat = format.getAudioFormat();
        return (int) (audioFormat.getFrameRate() * audioFormat.getFrameSize());
    }

}
