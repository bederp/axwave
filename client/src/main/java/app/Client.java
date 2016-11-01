package app;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import netty.decoders.SoundRecordMessageResponseDecoder;
import netty.encoders.SoundRecordMessageEncoder;
import netty.handlers.ClientHandler;
import recording.SoundRecord;
import recording.impl.MicrophoneSoundSource;
import soundformats.AudioFormatEnum;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;
import static soundformats.AudioFormatEnum.PCM_44100_16_STEREO_LE;

public class Client {

    private static final int NUMBER_OF_EXECUTOR_THREADS = 10;
    private static final int NUMBER_OF_ARGUMENTS = 2;
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 19000;

    public static void main(String[] args) throws InterruptedException {
        int k, n;

        if (args.length < NUMBER_OF_ARGUMENTS) {
            k = 2;
            n = 4;
        } else {
            k = Integer.parseInt(args[0]);
            n = Integer.parseInt(args[1]);

            if (k < 1 || n < 1) {
                System.out.printf("Wrong argument values K and N need to be > 0");
                return;
            }
        }

        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup);
        bootstrap.channel(NioSocketChannel.class);

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                final ChannelPipeline pipeline = ch.pipeline();
                pipeline.addLast(new SoundRecordMessageEncoder());
                pipeline.addLast(new SoundRecordMessageResponseDecoder());
                pipeline.addLast(new ClientHandler());
            }
        });

        final Channel channel = bootstrap.connect(SERVER_IP, SERVER_PORT).sync().channel();
        System.out.printf("Connected to ip %s on port %s\n", SERVER_IP, SERVER_PORT);

        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(NUMBER_OF_EXECUTOR_THREADS);
        executorService.scheduleAtFixedRate(recordSoundAndSendThroughChannel(n, channel, PCM_44100_16_STEREO_LE), 0, k, SECONDS);

    }

    private static Runnable recordSoundAndSendThroughChannel(int n, Channel channel, AudioFormatEnum audioFormat) {
        return () -> {
            final SoundRecord soundRecord = new MicrophoneSoundSource().recordSound(audioFormat, n);
            channel.writeAndFlush(soundRecord);
        };
    }
}
