package application.core.client.receivers.exceptions;

public class BodyReceiverException extends Exception {
    public BodyReceiverException() {
    }

    public BodyReceiverException(String message) {
        super(message);
    }

    public BodyReceiverException(String message, Throwable cause) {
        super(message, cause);
    }
}
