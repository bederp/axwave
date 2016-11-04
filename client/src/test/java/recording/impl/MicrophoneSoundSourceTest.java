package recording.impl;

import org.junit.Assert;
import org.junit.Test;
import recording.SoundRecord;
import soundformats.AudioFormatEnum;

import javax.sound.sampled.AudioFormat;

import static soundformats.AudioFormatEnum.PCM_44100_16_STEREO_LE;

public class MicrophoneSoundSourceTest {

    @Test
    public void testThatRecordedSamplesArrayHasExpectedSize() throws Exception {
        //Given
        final int secondsToRecord = 1;
        final AudioFormatEnum audioFormatEnum = PCM_44100_16_STEREO_LE;
        final AudioFormat audioFormat = audioFormatEnum.getAudioFormat();
        final int expectedBufferSize = (int) (secondsToRecord * audioFormat.getFrameRate() * audioFormat.getFrameSize());

        //When
        final SoundRecord record = new MicrophoneSoundSource(audioFormatEnum).recordSound(secondsToRecord);
        //Then
        Assert.assertEquals(expectedBufferSize, record.getSamples().length);
    }
}