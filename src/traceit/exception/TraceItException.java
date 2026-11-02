package traceit.exception;

public class TraceItException extends Exception {
    public TraceItException(String message) {
        super(message);
    }

    public TraceItException(String message, Throwable cause) {
        super(message, cause);
    }
}
