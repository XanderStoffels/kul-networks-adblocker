package application.responses;

import messaging.imp.HttpResponse;
import messaging.model.ResponseStatus;

public class HttpForbidden extends HttpResponse {
    public HttpForbidden(String message) {
        super(new ResponseStatus(403, "Forbidden"));
        this.setBody(message.getBytes());
    }
}
