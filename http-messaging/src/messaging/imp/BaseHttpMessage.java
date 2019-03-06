package messaging.imp;

import messaging.api.IHttpMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BaseHttpMessage implements IHttpMessage {

    protected Map<String,String> headers;
    protected byte[] body;

    protected BaseHttpMessage() {
        this.headers = new HashMap<>();
        this.body = new byte[] {};
    }

    protected boolean hasHeader(String header) {
        return this.headers.containsKey(header);
    }

    @Override
    public void setHeader(String header, String value) {
        if (hasHeader(header)) {
            this.headers.replace(header, value);
        } else {
            this.headers.put(header, value);
        }
    }

    @Override
    public String getHeaderValue(String header) {
        return this.headers.getOrDefault(header, null);
    }

    @Override
    public Set<String> getHeaders() {
        return this.headers.keySet();
    }

    @Override
    public void setBody(byte[] body) {
        this.body = body;
    }

    @Override
    public byte[] getBody() {
        return this.body;
    }


}
