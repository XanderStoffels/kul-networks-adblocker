package application.messaging.imp;

import application.messaging.api.IHttpResponse;
import application.messaging.model.ResponseStatus;

import java.util.Map;

public class HttpResponse extends BaseHttpMessage implements IHttpResponse {

    private ResponseStatus status;

    public HttpResponse(ResponseStatus status) {
        this.status = status;
    }

    public HttpResponse(ResponseStatus status, Map<String, String> headers, String body) {
        this(status);
        this.headers = headers;
        this.body = body;
    }

    public HttpResponse(ResponseStatus status, Map<String, String> headers, byte[] body){
        this(status);
        this.headers = headers;
        this.imageBody = body;
    }

    @Override
    public ResponseStatus getResponseStatus() {
        return this.status;
    }
}
