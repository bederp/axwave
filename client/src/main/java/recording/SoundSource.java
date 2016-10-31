package recording;

import soundformats.AudioFormatEnum;

/**
 * Created by kinder112 on 30.10.2016.
 */
public interface SoundSource {

    SoundRecord recordSound(AudioFormatEnum format, int seconds);

}
