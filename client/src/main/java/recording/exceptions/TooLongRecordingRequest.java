package recording.exceptions;

public class TooLongRecordingRequest extends RuntimeException {
    public TooLongRecordingRequest(String s) {
        super(s);
    }
}
