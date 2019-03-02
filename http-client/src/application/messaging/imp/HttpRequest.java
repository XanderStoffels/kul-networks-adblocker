package application.messaging.imp;

import application.messaging.HttpMethod;
import application.messaging.api.IHttpRequest;

import java.util.HashMap;

public class HttpRequest extends BaseHttpMessage implements IHttpRequest {

    private HttpMethod method;

    public HttpRequest(HttpMethod method) {
        this.method = method;
        this.headers = new HashMap<>();
    }

    @Override
    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    @Override
    public HttpMethod getMethod() {
        return this.method;
    }


}
