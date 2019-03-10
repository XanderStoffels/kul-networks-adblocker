package messaging.api;

import java.util.Map;

public interface IHttpHeaders {
    String getOrEmpty(String key);

    void set(String key, String value);

    boolean hasHeader(String key);

    Map<String, String> getValues();
}
