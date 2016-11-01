package writers;

import recording.SoundRecord;
import soundformats.AudioFormatEnum;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Saves given {@link SoundRecord} under {@link #USER_HOME} filepath <br>
 * Encodes metadata such as {@link SoundRecord#getAudioFormat()}, {@link SoundRecord#getTimestamp()} in the path <br>
 * Contents of the file are raw samples {@link SoundRecord#getSamples()}
 */
public class SoundRecordFileWriter implements DataWriter<SoundRecord> {

    private static final String USER_HOME = System.getProperty("user.home");
    private static final String RECORDING_FOLDER = "Recoded_Sounds";

    @Override
    public void write(SoundRecord soundRecord) {
        final Path pathToFile = createPathToFile(soundRecord);
        try {
            Files.createDirectories(pathToFile.getParent());
            Files.write(pathToFile, soundRecord.getSamples());
        } catch (IOException e) {
            System.out.println("Unable co create path " + pathToFile + "\n" +
                    "This soundRecord will be omitted");
        }
    }

    private Path createPathToFile(SoundRecord soundRecord) {
        final AudioFormatEnum audioFormat = soundRecord.getAudioFormat();
        final long timestamp = soundRecord.getTimestamp();
        return Paths.get(USER_HOME, RECORDING_FOLDER, audioFormat.name(), String.valueOf(timestamp) + audioFormat.getExtension());
    }
}
