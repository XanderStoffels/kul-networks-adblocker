package messaging.imp;


import messaging.api.IHttpResponse;
import messaging.model.ResponseStatus;

import java.util.Map;

public class HttpResponse extends BaseHttpMessage implements IHttpResponse {

    private ResponseStatus status;

    public HttpResponse(ResponseStatus status) {
        this.status = status;
    }

    public HttpResponse(ResponseStatus status, Map<String, String> headers, byte[] body) {
        this(status);
        this.headers = headers;
        this.body = body;
    }

    @Override
    public ResponseStatus getResponseStatus() {
        return this.status;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s %s %s\r\n",
                this.status.getHttpVersion(),
                this.status.getStatusCode(),
                this.status.getStatusMessage()));
        for (Map.Entry<String,String> kv : this.headers.entrySet()) {
            builder.append(String.format("%s: %s\r\n", kv.getKey(), kv.getValue()));
        }
        builder.append("\r\n");
        return builder.toString();
    }
}
