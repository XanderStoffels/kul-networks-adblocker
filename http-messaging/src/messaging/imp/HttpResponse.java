package messaging.imp;

import messaging.api.IHttpHeaders;
import messaging.api.IHttpResponse;
import messaging.model.HttpHeaders;
import messaging.model.ResponseStatus;
import java.util.Map;

public class HttpResponse extends BaseHttpMessage implements IHttpResponse {

    private ResponseStatus status;

    public HttpResponse(ResponseStatus status) {
        this.status = status;
        this.headers = new HttpHeaders();
        this.body = new byte[0];
    }

    public HttpResponse(ResponseStatus status, IHttpHeaders headers, byte[] body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    @Override
    public byte[] serialize() {
        // Status
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("%s %s %s\r\n",
                this.getHttpVersion(),
                this.status.getStatusCode(),
                this.status.getStatusMessage()));

        // Headers
        this.headers.getValues().forEach((key, value) -> {
            builder.append(String.format("%s: %s\r\n", key, value));
        });

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
