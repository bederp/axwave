package recording;

import soundformats.AudioFormatEnum;

public interface SoundRecord {
    AudioFormatEnum getAudioFormat();

    long getTimestamp();

    byte[] getSamples();
}
