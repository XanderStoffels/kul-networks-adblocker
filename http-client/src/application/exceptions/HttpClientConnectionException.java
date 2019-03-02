package application.exceptions;

public class HttpClientConnectionException extends Exception {
    public HttpClientConnectionException() {
    }

    public HttpClientConnectionException(String message) {
        super(message);
    }

    public HttpClientConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
