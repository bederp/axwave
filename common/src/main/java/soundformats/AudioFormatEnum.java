package soundformats;

import javax.sound.sampled.AudioFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kinder112 on 30.10.2016.
 */
public enum AudioFormatEnum {
    PCM_44100_16_STEREO_LE(44100, 16, 2, true, false, (short) 0x1218);

    float sampleRate;
    int sampleSizeInBits;
    int channels;
    boolean signed;
    boolean bigEndian;
    short formatEncoding;

    private static final Map<Short,AudioFormatEnum> map;
    static {
        map = new HashMap<>();
        for (AudioFormatEnum e : AudioFormatEnum.values()) {
            map.put(e.formatEncoding, e);
        }
    }

    AudioFormatEnum(float sampleRate, int sampleSizeInBits, int channels, boolean signed, boolean bigEndian, short formatEncoding) {
        this.sampleRate = sampleRate;
        this.sampleSizeInBits = sampleSizeInBits;
        this.channels = channels;
        this.signed = signed;
        this.bigEndian = bigEndian;
        this.formatEncoding = formatEncoding;
    }

    public AudioFormat getAudioFormat() {
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }

    public short getFormatEncoding() {
        return formatEncoding;
    }

    public static AudioFormatEnum findByFormatEncoding(short formatEncoding) {
        return map.get(formatEncoding);
    }
}
