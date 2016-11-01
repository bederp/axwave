package soundformats;

import javax.sound.sampled.AudioFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Enum that stores some common {@link AudioFormat} that are used within application <br>
 * Also provides some convinience methods
 */
public enum AudioFormatEnum {
    PCM_44100_16_STEREO_LE(44100, 16, 2, true, false, (short) 0x1218, Constants.PCM),
    PCM_8000_16_STEREO_LE(8000, 16, 2, true, false, (short) 0x1413, Constants.PCM),
    PCM_8000_8_STEREO_LE(8000, 8, 2, true, false, (short) 0x7ABC, Constants.PCM),
    PCM_8000_8_MONO_LE(8000, 8, 1, true, false, (short) 0x70BA, Constants.PCM);

    float sampleRate;
    int sampleSizeInBits;
    int channels;
    boolean signed;
    boolean bigEndian;
    short formatEncoding;
    private String extension;
    private static final Map<Short, AudioFormatEnum> map;

    static {
        map = new HashMap<>();
        for (AudioFormatEnum e : AudioFormatEnum.values()) {
            map.put(e.formatEncoding, e);
        }
    }

    AudioFormatEnum(float sampleRate, int sampleSizeInBits, int channels, boolean signed, boolean bigEndian, short formatEncoding, String extension) {
        this.sampleRate = sampleRate;
        this.sampleSizeInBits = sampleSizeInBits;
        this.channels = channels;
        this.signed = signed;
        this.bigEndian = bigEndian;
        this.formatEncoding = formatEncoding;
        this.extension = extension;
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

    public String getExtension() {
        return extension;
    }

    private static class Constants {
        static final String PCM = ".pcm";
    }
}
