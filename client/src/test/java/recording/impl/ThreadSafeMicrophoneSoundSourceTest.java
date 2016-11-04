package recording.impl;

import org.junit.Assert;
import org.junit.Test;
import recording.SoundRecord;
import soundformats.AudioFormatEnum;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static soundformats.AudioFormatEnum.PCM_8000_8_MONO_LE;

public class ThreadSafeMicrophoneSoundSourceTest {
    @Test
    public void shouldWork() throws Exception {
        final ThreadSafeMicrophoneSoundSource soundSource = new ThreadSafeMicrophoneSoundSource(PCM_8000_8_MONO_LE, 10);
        soundSource.start();
//        Thread.sleep(4000);

        final SoundRecord record = soundSource.recordSound(1, 3);

    }

    @Test
    public void twoCallableStartedAtSameTimeShouldRecordSameSamples() throws Exception {
        //Given
        final int secondsToRecord = 2;
        final AudioFormatEnum audioFormatEnum = PCM_8000_8_MONO_LE;
        ExecutorService pool = Executors.newFixedThreadPool(2);

        //When
        final Future<SoundRecord> soundRecordFuture1 = pool.submit(() -> new MicrophoneSoundSource(audioFormatEnum).recordSound(secondsToRecord));
        final Future<SoundRecord> soundRecordFuture2 = pool.submit(() -> new MicrophoneSoundSource(audioFormatEnum).recordSound(secondsToRecord));

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
            final SoundRecord record = new MicrophoneSoundSource(audioFormatEnum).recordSound(secondsToRecord);
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