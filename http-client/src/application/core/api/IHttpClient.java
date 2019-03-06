package application.core.api;

import application.exceptions.HttpClientConnectionException;
import messaging.api.IHttpRequest;
import messaging.api.IHttpResponse;

import java.io.IOException;

public interface IHttpClient {
    void connect() throws HttpClientConnectionException;
    void disconnect() throws HttpClientConnectionException;
    IHttpResponse request(IHttpRequest request) throws HttpClientConnectionException;
    IHttpResponse htmlRequest(IHttpRequest request) throws HttpClientConnectionException, IOException;

    String getBaseUrl();
    int getPort();

}
