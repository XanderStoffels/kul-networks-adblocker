package application.responses;

import messaging.imp.HttpResponse;
import messaging.model.ResponseStatus;

public class HttpCreated extends HttpResponse {
    public HttpCreated(String path) {
        super(new ResponseStatus(201, "Created"));
        this.getHeaders().set("Location", path);
        this.setBody("The resource has been created".getBytes());
    }
}
