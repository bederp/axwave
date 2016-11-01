package recording;

import soundformats.AudioFormatEnum;

public interface SoundSource {

    SoundRecord recordSound(AudioFormatEnum format, int seconds);

}
