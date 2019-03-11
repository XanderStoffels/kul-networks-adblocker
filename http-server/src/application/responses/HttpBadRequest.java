package application.responses;

import messaging.imp.HttpResponse;
import messaging.model.ResponseStatus;

public class HttpBadRequest extends HttpResponse {
    public HttpBadRequest(String message) {
        super(new ResponseStatus(400, "Bad Request"));
        this.setBody(message.getBytes());
    }
}
