package recording.impl;

import recording.SoundRecord;
import soundformats.AudioFormatEnum;

import java.io.ByteArrayOutputStream;

/**
 * Created by kinder112 on 30.10.2016.
 */
public class SoundRecordImpl implements SoundRecord {

    private AudioFormatEnum format;
    private ByteArrayOutputStream samples;
    private long timestamp;

    public SoundRecordImpl(AudioFormatEnum format, ByteArrayOutputStream data, long timestamp) {
        this.format = format;
        this.samples = data;
        this.timestamp = timestamp;
    }

    @Override
    public AudioFormatEnum getAudioFormat() {
        return format;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public ByteArrayOutputStream getSamples() {
        return samples;
    }
}
