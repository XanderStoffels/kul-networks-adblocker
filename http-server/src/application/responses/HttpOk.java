package application.responses;

import messaging.imp.HttpResponse;
import messaging.model.ResponseStatus;

public class HttpOk extends HttpResponse {
    public HttpOk() {
        super(new ResponseStatus(200, "Ok"));
    }
}
