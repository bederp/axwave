package recording.impl;

import recording.SoundRecord;
import recording.SoundSource;
import soundformats.AudioFormatEnum;

import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;

public class MicrophoneSoundSource implements SoundSource {

    private AudioFormatEnum format;
    private int seconds;
    private ByteArrayOutputStream out;
    private long timestamp;

    @Override
    public SoundRecord recordSound(AudioFormatEnum format, int seconds) {
        this.format = format;
        this.seconds = seconds;

        AudioFormat af = format.getAudioFormat();
        out = new ByteArrayOutputStream();

        try (TargetDataLine line = AudioSystem.getTargetDataLine(af)) {
            int recordingSize = calculateRequiredSize();

            byte[] data = new byte[line.getBufferSize() / 5];
            int numBytesRead;

            // Save timestamp
            timestamp = System.currentTimeMillis();

            // Begin audio capture.
            line.open(af);
            line.start();

            while (out.size() < recordingSize) {
                // Read the next chunk of data from the TargetDataLine.
                numBytesRead = line.read(data, 0, data.length);
                // Save this chunk of data.
                out.write(data, 0, numBytesRead);
            }
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

        return new SoundRecordImpl(format, out.toByteArray(), timestamp);
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
