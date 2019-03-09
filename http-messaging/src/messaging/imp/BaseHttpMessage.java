package messaging.imp;

import messaging.api.IHttpHeaders;
import messaging.api.IHttpMessage;
import messaging.model.HttpHeaders;

public class BaseHttpMessage implements IHttpMessage {

    public static final String standardHttpVersion = "HTTP/1.1";

    protected IHttpHeaders headers;
    protected byte[] body;
    protected String httpVersion;

    protected BaseHttpMessage() {
        this.headers = new HttpHeaders();
        this.body = new byte[] {};
    }

    @Override
    public void setBody(byte[] body) {
        this.body = body;
    }

    @Override
    public byte[] getBody() {
        return this.body;
    }

    @Override
    public String getHttpVersion() {
        return httpVersion;
    }

    @Override
    public IHttpHeaders getHeaders() {
        return this.headers;
    }

    @Override
    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }
}
