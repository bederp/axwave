package recording.impl;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import recording.SoundRecord;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static soundformats.AudioFormatEnum.PCM_8000_8_MONO_LE;

public class ThreadSafeMicrophoneSoundSourceTest {
    private static final int ONE_MINUTE_BUFFER = 60;
    private static ThreadSafeMicrophoneSoundSource soundSource;

    @BeforeClass
    public static void setUp() throws Exception {
        soundSource = new ThreadSafeMicrophoneSoundSource(PCM_8000_8_MONO_LE, ONE_MINUTE_BUFFER);
        soundSource.start();
    }

    @Test
    public void shouldRecordNSecondsOfSound() throws Exception {
        //Then
        final SoundRecord record = soundSource.recordSound(5);
    }

    @Test
    public void shouldRecordGivenSoundSlice() throws Exception {
        //Then
        final SoundRecord record = soundSource.recordSound(1, 3);
    }

    @Test
    public void shouldRecordSameSamples() throws Exception {
        //Given
        final int secondsToRecord = 2;
        ExecutorService pool = Executors.newFixedThreadPool(2);

        //When
        final Future<SoundRecord> soundRecordFuture1 = pool.submit(() -> soundSource.recordSound(secondsToRecord));
        final Future<SoundRecord> soundRecordFuture2 = pool.submit(() -> soundSource.recordSound(secondsToRecord));

        final SoundRecord soundRecord1 = soundRecordFuture1.get();
        final SoundRecord soundRecord2 = soundRecordFuture2.get();
        //Then
        Assert.assertEquals(soundRecord1, soundRecord2);
    }

    @Test
    public void shouldRecordSameSliceSamples() throws Exception {
        //Given
        final int from = 2;
        final int to = 8;
        ExecutorService pool = Executors.newFixedThreadPool(2);

        //When
        final Future<SoundRecord> soundRecordFuture1 = pool.submit(() -> soundSource.recordSound(from, to));
        final Future<SoundRecord> soundRecordFuture2 = pool.submit(() -> soundSource.recordSound(from, to));

        final SoundRecord soundRecord1 = soundRecordFuture1.get();
        final SoundRecord soundRecord2 = soundRecordFuture2.get();
        //Then
        Assert.assertEquals(soundRecord1, soundRecord2);
    }

    @Test
    public void addedSlicesShouldBeEqualToWholeRecord() throws Exception {
        //Given
        final int from = 2;
        final int middle = 4;
        final int to = 8;
        ExecutorService pool = Executors.newFixedThreadPool(3);

        //When
        final Future<SoundRecord> soundRecordFuture1 = pool.submit(() -> soundSource.recordSound(from, middle));
        final Future<SoundRecord> soundRecordFuture2 = pool.submit(() -> soundSource.recordSound(middle, to));
        final Future<SoundRecord> soundRecordFuture3 = pool.submit(() -> soundSource.recordSound(from, to));

        final SoundRecord soundRecord1 = soundRecordFuture1.get();
        final SoundRecord soundRecord2 = soundRecordFuture2.get();
        final SoundRecord soundRecord3 = soundRecordFuture3.get();

        final SoundRecord mergedRecord = mergeRecords(soundRecord1, soundRecord2);
        //Then
        Assert.assertEquals(soundRecord3, mergedRecord);
    }

    private SoundRecord mergeRecords(SoundRecord soundRecord1, SoundRecord soundRecord2) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(soundRecord1.getSamples());
        out.write(soundRecord2.getSamples());
        return new SoundRecordImpl(soundRecord1.getAudioFormat(), out.toByteArray(), soundRecord1.getTimestamp());
    }

}