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
    public byte[] serialize() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s %s %s\r\n",
                this.status.getHttpVersion(),
                this.status.getStatusCode(),
                this.status.getStatusMessage()));
        for (Map.Entry<String,String> kv : this.headers.entrySet()) {
            builder.append(String.format("%s: %s\r\n", kv.getKey(), kv.getValue()));
        }
        builder.append("\r\n");
        byte[] statusAndHeaderBytes = builder.toString().getBytes();

        // Combine status, headers and body into one byte array
        byte[] payload = new byte[statusAndHeaderBytes.length + this.getBody().length];
        System.arraycopy(statusAndHeaderBytes, 0, payload, 0,statusAndHeaderBytes.length);
        System.arraycopy(this.getBody(), 0, payload, statusAndHeaderBytes.length,this.getBody().length);

        return payload;
    }

    @Override
    public ResponseStatus getResponseStatus() {
        return this.status;
    }

}
