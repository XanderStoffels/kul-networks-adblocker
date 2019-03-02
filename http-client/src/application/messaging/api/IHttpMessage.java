package application.messaging.api;

import java.util.Set;

public interface IHttpMessage {
    void setHeader(String header, String value);
    String getHeaderValue(String header);
    Set<String> getHeaders();

    void setBody(String body);
    String getBody();
}
