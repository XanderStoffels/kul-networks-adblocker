package application.messaging.imp;

import application.messaging.api.IHttpResponse;
import application.messaging.model.ResponseStatus;

public class HttpResponse extends BaseHttpMessage implements IHttpResponse {

    private ResponseStatus status;

    public HttpResponse(ResponseStatus status) {
        this.status = status;
    }

    @Override
    public ResponseStatus getResponseStatus() {
        return this.status;
    }
}
