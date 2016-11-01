package recording;

import soundformats.AudioFormatEnum;

/**
 * Structure that represents recorded sound
 */
public interface SoundRecord {

    /**
     * Describes parameters of recorded sound <br>
     * Allowing you to interpret raw data as sound
     * @return AudioFormatEnum
     */
    AudioFormatEnum getAudioFormat();

    /**
     * Timestamp of when the first sample was recorded
     * @return long representing timestamp
     */
    long getTimestamp();

    /**
     * Array holding the recorded samples
     * @return byte[] of recorded samples
     */
    byte[] getSamples();
}
