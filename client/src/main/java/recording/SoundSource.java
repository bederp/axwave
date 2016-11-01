package recording;

import soundformats.AudioFormatEnum;

public interface SoundSource {

    /**
     * Record N seconds of sound with given {@link AudioFormatEnum}
     * @param format format to use when recording
     * @param seconds how many seconds to record
     * @return {@link SoundRecord}
     */
    SoundRecord recordSound(AudioFormatEnum format, int seconds);

}
