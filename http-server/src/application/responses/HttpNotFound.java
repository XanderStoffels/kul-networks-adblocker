package application.responses;

import messaging.imp.HttpResponse;
import messaging.model.ResponseStatus;

public class HttpNotFound extends HttpResponse {
    public HttpNotFound() {
        super(new ResponseStatus(404, "Not Found"));
    }
}
