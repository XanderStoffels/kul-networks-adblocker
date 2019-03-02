package application.messaging.api;

import java.util.Set;

public interface IHttpResponse extends IHttpMessage {
    int getStatusCode();
    String getStatusMessage();
}
