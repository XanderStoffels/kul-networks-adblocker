package application.core.client.api;

import application.exceptions.HttpClientException;
import messaging.api.IHttpRequest;
import messaging.api.IHttpResponse;

import java.io.IOException;

public interface IHttpClient {
    void connect() throws HttpClientException;

    void disconnect() throws HttpClientException;

    IHttpResponse request(IHttpRequest request) throws HttpClientException;

    String getBaseUrl();

    int getPort();
}
