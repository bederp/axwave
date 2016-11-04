package recording.impl;

import recording.SoundRecord;
import recording.SoundSource;
import recording.exceptions.TooLongRecordingRequest;
import recording.exceptions.TooOldRecordingRequest;
import soundformats.AudioFormatEnum;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link SoundSource} implementation which tries to record sound from microphone
 */
public class ThreadSafeMicrophoneSoundSource implements SoundSource{

    private static final int SECOND_TO_MILLIS = 1000;
    private AudioFormatEnum format;
    private final int oneSecondSize;
    private int bufferSize;
    volatile private long timestamp;
    volatile private int currentSecond;
    private int  samplesBufferPointer;
    private OneSecondSample[] samplesBuffer;
    private Map<Integer, Integer> secondToSample = new ConcurrentHashMap<>(bufferSize);
    private volatile boolean stopped = false;

    public ThreadSafeMicrophoneSoundSource(AudioFormatEnum format, int bufferSize) {
        this.format = format;
        this.bufferSize = bufferSize;
        samplesBuffer = new OneSecondSample[bufferSize];
        for (int i = 0; i <samplesBuffer.length ; i++) {
            samplesBuffer[i] = new OneSecondSample();
        }
        oneSecondSize = calculateOneSecondSampleSize();
    }

    public void start() {
        new Thread(this::startRecording).start();
    }

    private void startRecording() {
        try (TargetDataLine line = AudioSystem.getTargetDataLine(format.getAudioFormat())) {
            startLine(line);
            timestamp = System.currentTimeMillis();
            readSound(line);

        } catch (LineUnavailableException e) {
            System.out.println(e.getMessage());
        }
    }

    private void startLine(TargetDataLine line) throws LineUnavailableException {
        line.flush();
        line.open(format.getAudioFormat());
        line.start();
    }

    private void readSound(TargetDataLine line) {
        int numBytesRead;
        byte[] data = new byte[line.getBufferSize() / 5];
        ByteArrayOutputStream out = new ByteArrayOutputStream(oneSecondSize);
        while (!stopped) {
            while (out.size() < oneSecondSize) {
                numBytesRead = line.read(data, 0, data.length);
                out.write(data, 0, numBytesRead);
            }
            saveSampleToBuffer(out);
        }
    }

    private void saveSampleToBuffer(ByteArrayOutputStream out) {
        OneSecondSample sample = samplesBuffer[samplesBufferPointer];
        sample.timestamp = this.timestamp + currentSecond * SECOND_TO_MILLIS;
        sample.oneSecondSample = out.toByteArray();
        out.reset();
        updateSecondsToSampleMap();
        samplesBufferPointer = ++samplesBufferPointer%bufferSize;
        currentSecond++;
    }

    private void updateSecondsToSampleMap() {
        if(currentSecond >= bufferSize){
            secondToSample.remove(currentSecond - bufferSize);
        }
        secondToSample.put(currentSecond, samplesBufferPointer);
    }

    @Override
    public AudioFormatEnum getFormat() {
        return this.format;
    }

    public SoundRecord recordSound(int seconds) {

        long current = System.currentTimeMillis();
        sleep(seconds);

        int startingSecond = (int) ((current - timestamp) / SECOND_TO_MILLIS);
        int firstIndex = secondToSample.get(startingSecond);
        long timestampOfFirstSample = samplesBuffer[firstIndex].timestamp;
        final ByteBuffer byteBuffer = ByteBuffer.allocate(seconds * oneSecondSize);

        for (int i = 0; i < seconds; i++) {
            final int index = secondToSample.get(startingSecond + i);
            byteBuffer.put(samplesBuffer[index].oneSecondSample);
        }

        return new SoundRecordImpl(format, byteBuffer.array(), timestampOfFirstSample);
    }

    @Override
    public SoundRecord recordSound(int from, int to) {

        checkRecordingLength(from, to);
        checkOldness(from);
        sleepUntilAllDataIsAvailable(to);

        int length = to - from;
        int firstIndex = secondToSample.get(from);
        long timestampOfFirstSample = samplesBuffer[firstIndex].timestamp;
        final ByteBuffer byteBuffer = ByteBuffer.allocate(length * oneSecondSize);
        for (int i = 0; i < length; i++) {
            final int index = secondToSample.get(from + i);
            byteBuffer.put(samplesBuffer[index].oneSecondSample);
        }

        return new SoundRecordImpl(format, byteBuffer.array(), timestampOfFirstSample);
    }

    private void checkRecordingLength(int from, int to) {
        if(to - from > bufferSize){
            stop();
            throw new TooLongRecordingRequest("Requested recording length exceeds buffer size");
        }
    }

    private void checkOldness(int from) {
        if(from < Math.max(0, currentSecond - bufferSize)){
            stop();
            throw new TooOldRecordingRequest("Requested SoundRecord was already discarded from buffer as to old");
        }
    }

    private void sleep(int seconds) {
        try {
            Thread.sleep(seconds * SECOND_TO_MILLIS);
        } catch (InterruptedException e) {
            System.out.println("Recording thread got interrupted, this shouldn't happen please debug :)");
        }
    }

    private void sleepUntilAllDataIsAvailable(int to) {
        if(to > currentSecond){
            sleep(to - currentSecond);
        }
    }

    public void stop(){
        stopped = true;
    }

    /**
     * Calculate how much bytes do you need to store 1 second sample
     * @return number of bytes required to store 1 second
     */
    private int  calculateOneSecondSampleSize() {
        AudioFormat audioFormat = format.getAudioFormat();
        return (int) (audioFormat.getFrameRate() * audioFormat.getFrameSize());
    }

    private class OneSecondSample {
        long timestamp;
        byte[] oneSecondSample = new byte[oneSecondSize];
    }
}
