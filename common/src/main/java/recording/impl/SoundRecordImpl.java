package recording.impl;

import recording.SoundRecord;
import soundformats.AudioFormatEnum;

import java.util.Arrays;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SoundRecordImpl that = (SoundRecordImpl) o;

        if (timestamp != that.timestamp) return false;
        if (format != that.format) return false;
        return Arrays.equals(samples, that.samples);

    }

    @Override
    public int hashCode() {
        int result = format.hashCode();
        result = 31 * result + Arrays.hashCode(samples);
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }
}
