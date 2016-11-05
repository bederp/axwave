package concurrent;

import messages.MessageSizeExceeded;
import messages.SoundRecordMessage;
import recording.SoundRecord;
import recording.SoundSource;
import writers.DataWriter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.SECONDS;


/**
 * This class executes periodic recording on given {@link SoundSource}
 * with provided {@link #recordingLength} and {@link #recordingFrequentness} <br>
 * and writes the data into provided {@link DataWriter}
 */
public class FixedRateSoundWriter {
    private SoundSource source;
    private DataWriter<SoundRecordMessage> writer;
    private int recordingLength;
    private int recordingFrequentness;
    private AtomicInteger counter = new AtomicInteger();
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public FixedRateSoundWriter(SoundSource source, DataWriter<SoundRecordMessage> writer, int recordingLength, int recordingFrequentness) {
        this.source = source;
        this.writer = writer;
        this.recordingLength = recordingLength;
        this.recordingFrequentness = recordingFrequentness;
    }

    public void schedule() {
        final LongRunningTaskWrapper<Runnable> task = new LongRunningTaskWrapper<>(recordSoundAndSendData());
        executorService.scheduleAtFixedRate(task, 0, recordingFrequentness, SECONDS);
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

    /**
     * This is helper class for wrapping long running Runnable <br>
     * It is used when submitting to {@link FixedRateSoundWriter}
     * so that scheduled task executes quickly and <br>
     * long running work happens on another {@link Thread}
     * @param <T> Runnable to run
     */
    private static class LongRunningTaskWrapper<T extends Runnable>  implements Runnable {

        private T task;

        private LongRunningTaskWrapper(T task) {
            this.task = task;
        }

        @Override
        public void run() {
            new Thread(task).start();
        }
    }
}
