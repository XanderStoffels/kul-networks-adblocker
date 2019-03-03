package application.messaging.imp;

import application.messaging.model.HttpMethod;
import application.messaging.api.IHttpRequest;

import java.util.HashMap;

public class HttpRequest extends BaseHttpMessage implements IHttpRequest {

    public static final String httpVersion = "HTTP/1.1";

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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        // Set first line of the request
        builder.append(String.format("%s %s %s\r\n",
                getMethod().name(),
                getUrlTail(),
                httpVersion));

        // Set headers of the request
        getHeaders().forEach(h -> {
            builder.append(String.format("%s : %s\r\n", h, getHeaderValue(h)));
        });

        // End with an empty line
        builder.append("\r\n");

        return builder.toString();
    }
}
