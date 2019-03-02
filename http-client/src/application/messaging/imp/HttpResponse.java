package application.messaging.imp;

import application.messaging.api.IHttpResponse;

public class HttpResponse extends BaseHttpMessage implements IHttpResponse {

    private int statusCode;
    private String statusMessage;

    public HttpResponse(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
    }

    @Override
    public int getStatusCode() {
        return this.statusCode;
    }

    @Override
    public String getStatusMessage() {
        return this.getStatusMessage();
    }
}
