package data;

/**
 * any message and causing exception should not be shown to the end user. its debugging info only.
 */
public class LogException extends Exception {
    public LogException() {

    }

    public LogException(String message) {
        super(message);
    }

    public LogException(String message, Throwable cause) {
        super(message, cause);
    }

    public LogException(Throwable cause) {
        super(cause);
    }
}
