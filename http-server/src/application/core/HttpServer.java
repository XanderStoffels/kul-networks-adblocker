package application.core;

import application.exceptions.HttpServerException;
import messaging.api.IHttpRequest;
import messaging.api.IHttpResponse;
import messaging.imp.HttpRequest;
import messaging.imp.HttpResponse;
import messaging.model.ResponseStatus;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
    public void start(){
        if(serverSocket == null) return;
        running = true;

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
        while (isRunning()) {
            try {
                Socket client = serverSocket.accept();
                System.out.println("SOCKET CONNECTED");
                executor.submit(() -> handleClient(client));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void handleClient(Socket clientSocket) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());

            while (true) {
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

                String sRequest = String.join("\r\n", contents);
                IHttpRequest request = HttpRequest.parse(sRequest);
                System.out.println("INCOMING REQUEST");
                IHttpResponse response = this.handleClientRequest(clientSocket, request);
                response.setHeader("Connection","keep-alive");
                response.setHeader("Date", new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));

                String responseString = response.toString();
                writer.append(responseString);

                clientSocket.getOutputStream().write(response.getBody());
                writer.write("\r\n");
                writer.flush();
                clientSocket.close();
            }

           // clientSocket.close();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SocketException s) {
            System.out.println("CLIENT CLOSED THE CONNECTION");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private IHttpResponse handleClientRequest(Socket client, IHttpRequest request) {
        switch (request.getMethod()){
            case HEAD:
                return handleGetRequest(request, false);
            case GET:
                return handleGetRequest(request, true);
            case POST:

                break;
            case PUT:

                break;
                default:
                    //Todo fix
                    return null;
        }

        return null;

    }

    private IHttpResponse handleGetRequest(IHttpRequest request, boolean loadBody) {

        final String contentPath = "C:\\Users\\Xander\\Documents\\Projects\\kul-networks-adblocker\\http-server\\static";

        if (request.getUrlTail().equals("/") ||request.getUrlTail().equals("/index")) {
            request.setUrlTail("/index.html");
        }

        request.setUrlTail(request.getUrlTail().substring(1));
        Path requestPath = Paths.get(contentPath, request.getUrlTail());
        File requestFile = requestPath.toFile();

        if (!requestFile.exists()) {
            ResponseStatus status = new ResponseStatus(404);
            status.setHttpVersion("HTTP/1.1");
            status.setStatusMessage("NOT FOUND");
            return new HttpResponse(status);
        }

        try {
            byte[] fileData = Files.readAllBytes(requestPath);

            ResponseStatus status = new ResponseStatus(200);
            status.setHttpVersion("HTTP/1.1");
            status.setStatusMessage("OK");

            String contentType = Files.probeContentType(requestPath);

            IHttpResponse response = new HttpResponse(status);
            response.setHeader("Content-Length", String.valueOf(fileData.length));
            response.setHeader("Content-Type", contentType);
            if (loadBody) response.setBody(fileData);
            return response;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

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
