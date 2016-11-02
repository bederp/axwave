package app;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import messages.MessageSizeExceeded;
import messages.SoundRecordMessage;
import netty.handlers.ClientChannelInitializer;
import recording.SoundRecord;
import recording.impl.MicrophoneSoundSource;
import soundformats.AudioFormatEnum;
import writers.DataWriter;
import writers.SoundRecordMessageChannelWriter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;
import static soundformats.AudioFormatEnum.PCM_8000_8_MONO_LE;

public class Client {

    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 19000;

    private static final int NUMBER_OF_EXECUTOR_THREADS = 10;
    private static final int NUMBER_OF_ARGUMENTS = 2;
    private static final int DEFAULT_RECORDING_FREQUENTNESS = 1;
    private static final int DEFAULT_RECORDING_LENGTH = 3;

    public static void main(String[] args) throws InterruptedException {
        int recordingFrequentness, recordingLength;

        if (args.length < NUMBER_OF_ARGUMENTS) {
            recordingFrequentness = DEFAULT_RECORDING_FREQUENTNESS;
            recordingLength = DEFAULT_RECORDING_LENGTH;
        } else {
            recordingFrequentness = Integer.parseInt(args[0]);
            recordingLength = Integer.parseInt(args[1]);
        }

        checkForCorrectArgumentValues(recordingFrequentness, recordingLength);
        System.out.printf("recordingFrequentness %d seconds, recordingLength %d seconds\n", recordingFrequentness, recordingLength);

        Bootstrap bootstrap = getBootstrap();
        final Channel channel = bootstrap.connect(SERVER_IP, SERVER_PORT).sync().channel();
        System.out.printf("Connected to ip %s on port %s\n", SERVER_IP, SERVER_PORT);


        DataWriter<SoundRecordMessage> writer = new SoundRecordMessageChannelWriter(channel);
        final Runnable runnable = recordSoundAndSendData(recordingLength, writer, PCM_8000_8_MONO_LE);

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(NUMBER_OF_EXECUTOR_THREADS);
        executorService.scheduleAtFixedRate(runnable, 0, recordingFrequentness, SECONDS);

    }

    private static void checkForCorrectArgumentValues(int recordingFrequentness, int recordingLength) {
        if (recordingFrequentness < 1 || recordingLength < 1) {
            System.out.printf("Wrong argument values recordingFrequentness and recordingLength need to be > 0");
            System.exit(1);
        }
    }

    private static Bootstrap getBootstrap() {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ClientChannelInitializer());
        return bootstrap;
    }

    private static Runnable recordSoundAndSendData(int recordingLength, DataWriter<SoundRecordMessage> writer, AudioFormatEnum audioFormat) {
        return () -> {
            try {
                SoundRecord soundRecord = new MicrophoneSoundSource().recordSound(audioFormat, recordingLength);
                SoundRecordMessage msg = new SoundRecordMessage(soundRecord);
                System.out.println("Sending: " + msg);
                writer.write(msg);
            } catch (MessageSizeExceeded messageSizeExceeded) {
                System.out.printf("Recording in %s for %s seconds produces " +
                                "too big samples for sending with specified format\n" +
                                "Please change AudioFormat or recordingLength to smaller values.",
                        audioFormat, recordingLength);
                System.exit(1);
            }
        };
    }
}
