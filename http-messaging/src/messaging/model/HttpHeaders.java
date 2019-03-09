package messaging.model;

import messaging.api.IHttpHeaders;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpHeaders implements IHttpHeaders {
    private Map<String, String> headers;

    public HttpHeaders() {
        this.headers = new HashMap<>();
    }

    @Override
    public String getOrEmpty(String key) {
        return this.headers.getOrDefault(key, "");
    }

    @Override
    public void set(String key, String value) {
        if (this.hasHeader(key)){
            this.headers.replace(key, value);
        } else {
            this.headers.put(key, value);
        }
    }

    @Override
    public boolean hasHeader(String key) {
        return this.headers.containsKey(key);
    }

    @Override
    public Map<String, String> getValues(){
        return Collections.unmodifiableMap(this.headers);
    }

}
