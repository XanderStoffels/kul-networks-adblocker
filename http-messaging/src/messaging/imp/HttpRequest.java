package messaging.imp;

import messaging.api.IHttpRequest;
import messaging.model.HttpMethod;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequest extends BaseHttpMessage implements IHttpRequest {

    public static final String httpVersion = "HTTP/1.1";

    private HttpMethod method;
    private String urlTail;

    public HttpRequest(HttpMethod method) {
        this.method = method;
        this.headers = new HashMap<>();
        this.urlTail = "/";
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
        builder.append(String.format("%s %s %s\r\n", method.name(), this.urlTail, httpVersion));
        for (Map.Entry<String, String> header : this.headers.entrySet()) {
            builder.append(String.format("%s: %s\r\n", header.getKey(), header.getValue()));
        }
        builder.append("\r\n");

        return builder.toString().getBytes();
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

        if (this.method != HttpMethod.GET && this.method != HttpMethod.HEAD) {
            builder.append(new String(this.body));
        }


        return builder.toString();
    }

    public static HttpRequest parse(String requestString) {
        String[] parts =  requestString.split("\r\n");
        if(parts.length < 1 )
            throw new IllegalArgumentException("Unable to parse string");

        String[] statusParts = parts[0].split(" ");
        HttpRequest httpRequest = new HttpRequest();

        if(statusParts[0].equals(HttpMethod.GET.name())) {
            httpRequest.setMethod(HttpMethod.GET);

        }else if (statusParts[0].equals(HttpMethod.HEAD.name())){
            httpRequest.setMethod(HttpMethod.HEAD);

        }else if (statusParts[0].equals(HttpMethod.PUT.name())){
            httpRequest.setMethod(HttpMethod.PUT);

        }else if (statusParts[0].equals(HttpMethod.POST.name())){
            httpRequest.setMethod(HttpMethod.POST);
        } else {
            throw new IllegalArgumentException("Http method not supported");
        }

        httpRequest.setUrlTail(statusParts[1]);
        if(parts.length == 1)
            return httpRequest;

        //TODO refactor duplicate

        for (String l : Arrays.stream(parts).skip(1).collect(Collectors.toList())) {
            int index = l.indexOf(':');
            if (index < 0) {
                continue;
            }
            String key = l.substring(0, index).trim();
            String value = l.substring(index + 1).trim();
            httpRequest.setHeader(key, value);
        }



        return httpRequest;
    }
}
