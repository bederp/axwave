package writers;

import io.netty.channel.Channel;
import messages.SoundRecordMessage;

/**
 * {@link DataWriter} implementation which handles {@link SoundRecordMessage} <br>
 *  and writing to netty {@link Channel}
 */
public class SoundRecordMessageChannelWriter implements DataWriter<SoundRecordMessage> {

    private Channel channel;

    public SoundRecordMessageChannelWriter(Channel channel) {
        this.channel = channel;
    }

    @Override
    public void write(SoundRecordMessage data) {
        channel.writeAndFlush(data);
    }
}
