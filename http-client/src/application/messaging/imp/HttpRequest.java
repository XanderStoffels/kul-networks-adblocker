package application.messaging.imp;

import application.messaging.HttpMethod;
import application.messaging.api.IHttpRequest;

import java.util.HashMap;

public class HttpRequest extends BaseHttpMessage implements IHttpRequest {

    private HttpMethod method;
    private String urlTail;

    public HttpRequest(HttpMethod method) {
        this.method = method;
        this.headers = new HashMap<>();
        this.urlTail = "/";
    }

    @Override
    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    @Override
    public HttpMethod getMethod() {
        return this.method;
    }

    @Override
    public void setUrlTail(String tail) {
        this.urlTail = tail;
    }

    @Override
    public String getUrlTail() {
        return this.urlTail;
    }


}
