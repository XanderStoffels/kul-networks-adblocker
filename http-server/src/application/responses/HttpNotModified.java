package application.responses;

import messaging.imp.HttpResponse;
import messaging.model.ResponseStatus;

public class HttpNotModified extends HttpResponse {
    public HttpNotModified() {
        super(new ResponseStatus(304, "Not Modified"));
    }
}
