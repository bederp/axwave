package recording.impl;

import recording.SoundRecord;
import recording.SoundSource;
import soundformats.AudioFormatEnum;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;

/**
 * {@link SoundSource} implementation which tries to record sound from microphone
 */
public class MicrophoneSoundSource implements SoundSource {

    private AudioFormatEnum format;
    private int seconds;
    private ByteArrayOutputStream out = new ByteArrayOutputStream();
    private long timestamp;

    @Override
    public SoundRecord recordSound(AudioFormatEnum format, int seconds) {
        this.format = format;
        this.seconds = seconds;

        try (TargetDataLine line = AudioSystem.getTargetDataLine(format.getAudioFormat())) {
            startLineRecording(line);
            timestamp = System.currentTimeMillis();
            readSound(line);

        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

        return new SoundRecordImpl(format, out.toByteArray(), timestamp);
    }

    private void startLineRecording(TargetDataLine line) throws LineUnavailableException {
        line.flush();
        line.open(format.getAudioFormat());
        line.start();
    }

    private void readSound(TargetDataLine line) {
        int recordingSize = calculateRequiredSize();
        int numBytesRead;
        byte[] data = new byte[line.getBufferSize() / 5];

        while (out.size() < recordingSize) {
            // Read the next chunk of data from the TargetDataLine.
            numBytesRead = line.read(data, 0, data.length);
            // Save this chunk of data.
            out.write(data, 0, numBytesRead);
        }
    }

    /**
     * Calculate how much bytes do you need to record N seconds in given format
     * @return number of bytes required to store N seconds in desired format
     */
    private int calculateRequiredSize() {
        AudioFormat audioFormat = format.getAudioFormat();
        return (int) (seconds * audioFormat.getFrameRate() * audioFormat.getFrameSize());
    }

}
