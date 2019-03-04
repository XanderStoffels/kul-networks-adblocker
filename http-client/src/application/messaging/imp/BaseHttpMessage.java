package application.messaging.imp;

import application.messaging.api.IHttpMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BaseHttpMessage implements IHttpMessage {

    protected Map<String,String> headers;
    protected String body;
    protected byte[] imageBody;

    protected BaseHttpMessage() {
        this.headers = new HashMap<>();
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
    public void setBody(String body) {
        this.body = body;
        setHeader("Content-Length", Integer.toString(body.length()));
    }

    @Override
    public String getBody() {
        return this.body;
    }


}
