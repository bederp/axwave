package messages;

public class MessageSizeExceeded extends RuntimeException {
    MessageSizeExceeded(String s) {
        super(s);
    }
}
