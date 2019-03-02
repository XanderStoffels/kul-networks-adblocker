package application;

import application.messaging.api.IHttpRequest;
import application.messaging.api.IHttpResponse;

public interface IHttpClient {
    void connect(String baseAddress, int port);
    void disconnect();
    IHttpResponse send(IHttpRequest request);

}
