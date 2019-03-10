package application.responses;

import messaging.imp.HttpResponse;
import messaging.model.ResponseStatus;

public class HttpServerError extends HttpResponse {
    public HttpServerError(String message) {
        super(new ResponseStatus(500, "Internal Server Error"));
        this.setBody(message.getBytes());
    }
}