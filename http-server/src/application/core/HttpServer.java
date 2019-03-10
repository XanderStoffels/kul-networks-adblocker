package application.core;

import application.core.clientHandlers.api.IHttpClientHandler;
import application.core.clientHandlers.imp.PersistentHttpClientHandler;
import application.exceptions.HttpServerException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class HttpServer implements IHttpServer {
    private ServerSocket serverSocket;
    private boolean running;

    @Override
    public void setup() throws HttpServerException {

        try {
            serverSocket = new ServerSocket(80);
        } catch (IOException e) {
            throw new HttpServerException("Could not create server socket.", e);
        }
    }

    @Override
    public void start() {
        if (serverSocket == null) return;
        running = true;

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        while (isRunning()) {
            try {
                Socket client = serverSocket.accept();
                executor.submit(() -> {
                    IHttpClientHandler handler = new PersistentHttpClientHandler();
                    handler.handle(client);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop() throws HttpServerException {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                throw new HttpServerException("Could not close the server socket.", e);
            } finally {
                serverSocket = null;
                running = false;
            }
        }
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
