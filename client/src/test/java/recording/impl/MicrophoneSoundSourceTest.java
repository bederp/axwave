package recording.impl;

import org.junit.Assert;
import org.junit.Test;
import recording.SoundRecord;
import soundformats.AudioFormatEnum;

import javax.sound.sampled.AudioFormat;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static soundformats.AudioFormatEnum.PCM_44100_16_STEREO_LE;
import static soundformats.AudioFormatEnum.PCM_8000_8_MONO_LE;

public class MicrophoneSoundSourceTest {

    @Test
    public void testThatRecordedSamplesArrayHasExpectedSize() throws Exception {
        //Given
        final int secondsToRecord = 1;
        final AudioFormatEnum audioFormatEnum = PCM_44100_16_STEREO_LE;
        final AudioFormat audioFormat = audioFormatEnum.getAudioFormat();
        final int expectedBufferSize = (int) (secondsToRecord * audioFormat.getFrameRate() * audioFormat.getFrameSize());

        //When
        final SoundRecord record = new MicrophoneSoundSource().recordSound(audioFormatEnum, secondsToRecord);
        //Then
        Assert.assertEquals(expectedBufferSize, record.getSamples().length);
    }

    @Test
    public void twoCallableStartedAtSameTimeShouldRecordSameSamples() throws Exception {
        //Given
        final int secondsToRecord = 2;
        final AudioFormatEnum audioFormatEnum = PCM_8000_8_MONO_LE;
        ExecutorService pool = Executors.newFixedThreadPool(2);

        //When
        final Future<SoundRecord> soundRecordFuture1 = pool.submit(() -> new MicrophoneSoundSource().recordSound(audioFormatEnum, secondsToRecord));
        final Future<SoundRecord> soundRecordFuture2 = pool.submit(() -> new MicrophoneSoundSource().recordSound(audioFormatEnum, secondsToRecord));

        final SoundRecord soundRecord1 = soundRecordFuture1.get();
        final SoundRecord soundRecord2 = soundRecordFuture2.get();
        //Then
        Assert.assertArrayEquals(soundRecord1.getSamples(), soundRecord2.getSamples());
    }

    @Test
    public void twoThreadsStartedAtSameTimeShouldRecordSameSamples() throws Exception {
        //Given
        final int secondsToRecord = 1;
        final AudioFormatEnum audioFormatEnum = PCM_8000_8_MONO_LE;
        final Path file1 = Paths.get(System.getProperty("user.dir"), "tmp1.pcm");
        final Path file2 = Paths.get(System.getProperty("user.dir"), "tmp2.pcm");

        //When
        final Thread t1 = startRecordingThreadSavingToFile(secondsToRecord, audioFormatEnum, file1);
        final Thread t2 = startRecordingThreadSavingToFile(secondsToRecord, audioFormatEnum, file2);

        t1.join();
        t2.join();
        //Then
        final byte[] bytes1 = Files.readAllBytes(file1);
        final byte[] bytes2 = Files.readAllBytes(file2);
        Assert.assertArrayEquals(bytes1, bytes2);
    }

    private Thread startRecordingThreadSavingToFile(int secondsToRecord, AudioFormatEnum audioFormatEnum, Path path) {
        final Thread thread = new Thread(() -> {
            final SoundRecord record = new MicrophoneSoundSource().recordSound(audioFormatEnum, secondsToRecord);
            try {
                Files.write(path, record.getSamples());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        return thread;
    }
}