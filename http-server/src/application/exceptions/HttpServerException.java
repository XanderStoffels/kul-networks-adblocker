package application.exceptions;

public class HttpServerException extends Exception{
    public HttpServerException() {
    }

    public HttpServerException(String message) {
        super(message);
    }

    public HttpServerException(String message, Throwable cause) {
        super(message, cause);
    }
}
