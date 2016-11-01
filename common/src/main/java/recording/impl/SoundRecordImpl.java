package recording.impl;

import recording.SoundRecord;
import soundformats.AudioFormatEnum;

/**
 * Simple POJO implementing {@link SoundRecord}
 */
public class SoundRecordImpl implements SoundRecord {

    private AudioFormatEnum format;
    private byte[] samples;
    private long timestamp;

    public SoundRecordImpl(AudioFormatEnum format, byte[] samples, long timestamp) {
        this.format = format;
        this.samples = samples;
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
    public byte[] getSamples() {
        return samples;
    }
}
