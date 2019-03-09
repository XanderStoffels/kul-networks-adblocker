package messaging.imp;

import messaging.api.IHttpRequest;
import messaging.model.HttpHeaders;
import messaging.model.HttpMethod;

import java.util.Arrays;
import java.util.stream.Collectors;

public class HttpRequest extends BaseHttpMessage implements IHttpRequest {

    private HttpMethod method;
    private String urlTail;

    public HttpRequest(HttpMethod method) {
        this.method = method;
        this.headers = new HttpHeaders();
        this.urlTail = "/";
        this.setHttpVersion(BaseHttpMessage.standardHttpVersion);
    }

    private HttpRequest(){

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
    public byte[] serialize() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s %s %s\r\n", method.name(), this.urlTail, standardHttpVersion));
        this.headers.getValues().forEach((key, value) -> {
            builder.append(String.format("%s: %s\r\n", key, value));
        });

        builder.append("\r\n");
        return builder.toString().getBytes();
    }

    public static HttpRequest parse(String requestString) {
        String[] parts =  requestString.split("\r\n");
        if(parts.length < 1 )
            throw new IllegalArgumentException("Unable to parse string");

        String[] statusParts = parts[0].split(" ");
        HttpRequest httpRequest = new HttpRequest();

        try {
            httpRequest.setMethod(HttpMethod.valueOf(statusParts[0]));
        } catch (Exception e) {
            throw new IllegalArgumentException("Http method not supported");
        }

        httpRequest.setUrlTail(statusParts[1]);
        if(parts.length == 1)
            return httpRequest;

        for (String l : Arrays.stream(parts).skip(1).collect(Collectors.toList())) {
            int index = l.indexOf(':');
            if (index < 0) {
                continue;
            }
            String key = l.substring(0, index).trim();
            String value = l.substring(index + 1).trim();
            httpRequest.getHeaders().set(key, value);
        }
        return httpRequest;
    }
}
