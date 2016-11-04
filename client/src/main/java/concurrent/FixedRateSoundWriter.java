package concurrent;

import messages.MessageSizeExceeded;
import messages.SoundRecordMessage;
import recording.SoundRecord;
import recording.SoundSource;
import writers.DataWriter;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.Executors.newScheduledThreadPool;
import static java.util.concurrent.TimeUnit.SECONDS;

public class FixedRateSoundWriter {
    private static final int NUMBER_OF_EXECUTOR_THREADS = 10;

    private SoundSource source;
    private DataWriter<SoundRecordMessage> writer;
    private int recordingLength;
    private int recordingFrequentness;
    private AtomicInteger counter = new AtomicInteger();
    private ScheduledExecutorService executorService = newScheduledThreadPool(NUMBER_OF_EXECUTOR_THREADS);

    public FixedRateSoundWriter(SoundSource source, DataWriter<SoundRecordMessage> writer, int recordingLength, int recordingFrequentness) {
        this.source = source;
        this.writer = writer;
        this.recordingLength = recordingLength;
        this.recordingFrequentness = recordingFrequentness;
    }

    public void schedule() {
        executorService.scheduleAtFixedRate(recordSoundAndSendData(), 0, recordingFrequentness, SECONDS);
    }

    private Runnable recordSoundAndSendData() {
        return () -> {
            try {
                final int from = counter.getAndIncrement() * recordingFrequentness;
                final SoundRecord soundRecord = source.recordSound(from, from + recordingLength);
                SoundRecordMessage msg = new SoundRecordMessage(soundRecord);
                System.out.println("Sending: " + msg);
                writer.write(msg);

            } catch (MessageSizeExceeded messageSizeExceeded) {
                System.out.printf("Recording in %s for %s seconds produces " +
                                "too many samples for sending with specified format\n" +
                                "Please change AudioFormat or recordingLength to smaller values.",
                        source.getFormat(), recordingLength);
                System.exit(1);
            }
        };
    }
}
