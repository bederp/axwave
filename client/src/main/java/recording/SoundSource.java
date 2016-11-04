package recording;

import soundformats.AudioFormatEnum;


public interface SoundSource {

    AudioFormatEnum getFormat();

    /**
     * Record N seconds of sound
     * @param seconds how many seconds to record
     * @return {@link SoundRecord}
     */
    SoundRecord recordSound(int seconds);


    /**
     * Records (to - from) seconds starting at from time
     * @param from second from which to start recording
     * @param to second to which record
     * @return  {@link SoundRecord} consisting of sound between from and to seconds
     */
    SoundRecord recordSound(int from, int to);

}
