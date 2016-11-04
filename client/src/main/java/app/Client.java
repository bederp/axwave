package app;

import concurrent.FixedRateSoundWriter;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import messages.SoundRecordMessage;
import netty.handlers.ClientChannelInitializer;
import recording.impl.ThreadSafeMicrophoneSoundSource;
import writers.DataWriter;
import writers.SoundRecordMessageChannelWriter;

import static soundformats.AudioFormatEnum.PCM_8000_8_MONO_LE;

public class Client {

    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 19000;

    private static final int NUMBER_OF_ARGUMENTS = 2;
    private static final int DEFAULT_RECORDING_FREQUENTNESS = 2;
    private static final int DEFAULT_RECORDING_LENGTH = 4;

    private static int recordingFrequentness;
    private static int recordingLength;

    public static void main(String[] args) throws InterruptedException {
        checkArguments(args);
        System.out.printf("recordingFrequentness %d seconds, recordingLength %d seconds\n", recordingFrequentness, recordingLength);

        Bootstrap bootstrap = getBootstrap();
        final Channel channel = bootstrap.connect(SERVER_IP, SERVER_PORT).sync().channel();
        DataWriter<SoundRecordMessage> writer = new SoundRecordMessageChannelWriter(channel);
        System.out.printf("Connected to ip %s on port %s\n", SERVER_IP, SERVER_PORT);

        final ThreadSafeMicrophoneSoundSource source = new ThreadSafeMicrophoneSoundSource(PCM_8000_8_MONO_LE, recordingLength * 10);
        source.start();

        new FixedRateSoundWriter(source, writer, recordingLength, recordingFrequentness).schedule();
    }

    private static void checkArguments(String[] args) {
        if (args.length < NUMBER_OF_ARGUMENTS) {
            recordingFrequentness = DEFAULT_RECORDING_FREQUENTNESS;
            recordingLength = DEFAULT_RECORDING_LENGTH;
        } else {
            recordingFrequentness = Integer.parseInt(args[0]);
            recordingLength = Integer.parseInt(args[1]);
        }
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

}
