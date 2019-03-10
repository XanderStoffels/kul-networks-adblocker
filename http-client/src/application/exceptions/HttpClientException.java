package application.exceptions;

public class HttpClientException extends Exception {
    public HttpClientException() {
    }

    public HttpClientException(String message) {
        super(message);
    }

    public HttpClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
