package application;

import application.exceptions.HttpClientConnectionException;
import application.messaging.api.IHttpRequest;
import application.messaging.api.IHttpResponse;

public interface IHttpClient {
    void connect() throws HttpClientConnectionException;
    void disconnect() throws HttpClientConnectionException;
    IHttpResponse send(IHttpRequest request) throws HttpClientConnectionException;

    String getBaseUrl();
    int getPort();

}
