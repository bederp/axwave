package recording;

import soundformats.AudioFormatEnum;

import java.io.ByteArrayOutputStream;

/**
 * Created by kinder112 on 30.10.2016.
 */
public interface SoundRecord {
    AudioFormatEnum getAudioFormat();

    long getTimestamp();

    ByteArrayOutputStream getSamples();
}
