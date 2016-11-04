package recording.exceptions;

public class TooOldRecordingRequest extends RuntimeException {
    public TooOldRecordingRequest(String s) {
        super(s);
    }
}
