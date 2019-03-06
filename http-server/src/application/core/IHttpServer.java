package application.core;

import application.exceptions.HttpServerException;

import java.io.IOException;

public interface IHttpServer {
    void setup() throws IOException, HttpServerException;
    void start();
    void stop() throws HttpServerException;
    boolean isRunning();
}
