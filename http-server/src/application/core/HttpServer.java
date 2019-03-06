package application.core;

import application.exceptions.HttpServerException;
import messaging.api.IHttpRequest;
import messaging.imp.HttpRequest;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

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
    public void start(){
        if(serverSocket == null) return;
        running = true;

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        while (isRunning()) {
            try {
                Socket client = serverSocket.accept();

                executor.submit(() -> handleClient(client));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void handleClient(Socket clientSocket) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            boolean contentAvailable = true;
            List<String> contents = new ArrayList<>();

            while (contentAvailable) {
                String content = reader.readLine();
                if(content.length() == 0) {
                    contentAvailable = false;
                } else {
                    contents.add(content);
                }
            }
            String sRequest = contents.stream().collect(Collectors.joining("\r\n"));
            IHttpRequest request = HttpRequest.parse(sRequest);

            System.out.println(request.getMethod());
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
            return;
        }
    }

    @Override
    public void stop() throws HttpServerException {
        if(serverSocket != null) {
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
