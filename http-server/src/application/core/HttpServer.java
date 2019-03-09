package application.core;

import application.exceptions.HttpServerException;
import application.responses.*;
import messaging.api.IHttpRequest;
import messaging.api.IHttpResponse;
import messaging.imp.HttpRequest;
import messaging.imp.HttpResponse;
import messaging.model.HttpMethod;
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
    public void start() {
        if (serverSocket == null) return;
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
        int requestCount = 0;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream writer = clientSocket.getOutputStream();

            boolean keepAlive = true;

            while (keepAlive) {
                boolean contentAvailable = true;
                List<String> contents = new ArrayList<>();

                // Read incoming request
                while (contentAvailable) {
                    String content = reader.readLine();
                    if (content.length() == 0) {
                        contentAvailable = false;
                    } else {
                        contents.add(content);
                    }
                }

                // Parse the request
                String sRequest = String.join("\r\n", contents);
                IHttpRequest request = HttpRequest.parse(sRequest);

                keepAlive = "Keep-Alive".equals(request.getHeaderValue("Connection"));

                if (request.getMethod() != HttpMethod.GET && request.getMethod() != HttpMethod.HEAD) {
                    int length = Integer.parseInt(request.getHeaderValue("Content-Length"));

                    StringBuilder builder = new StringBuilder();
                    while (builder.toString().length() != length)
                        builder.append((char) reader.read());

                    request.setBody(builder.toString().getBytes());
                }

                requestCount++;

                // Generate response
                IHttpResponse response;

                try {
                    response = this.handleClientRequest(request);
                } catch (Exception e) {
                    response = new HttpServerError(String.format("%s\n%s", "An internal server error occurred", e.getMessage()));
                }

                response.setHeader("Date", new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));

                if (!response.getHeaders().contains("Content-Length"))
                    response.setHeader("Content-Length", String.valueOf(response.getBody().length));

                if (keepAlive)
                    response.setHeader("Connection", "Keep-Alive");

                // Serialize response
                byte[] responseBytes = response.serialize();

                // Write the response to the stream
                writer.write(responseBytes);
                writer.flush();
            }

            clientSocket.close();

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SocketException s) {
            System.out.println("CLIENT CLOSED THE CONNECTION");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private IHttpResponse handleClientRequest(IHttpRequest request) throws IOException {
        switch (request.getMethod()) {
            case HEAD:
                return handleGetRequest(request, false);
            case GET:
                return handleGetRequest(request, true);
            case PUT:
                return handlePutRequest(request);
            case POST:

                break;
            default:
                //Todo fix
                return null;
        }

        return null;

    }

    private IHttpResponse handleGetRequest(IHttpRequest request, boolean loadBody) {

        System.out.println(request.getUrlTail());
        if (request.getUrlTail().equals("/") || request.getUrlTail().equals("/index")) {
            request.setUrlTail("/index.html");
        }

        request.setUrlTail(request.getUrlTail().substring(1));
        Path requestPath = Paths.get("static", request.getUrlTail());
        File requestFile = requestPath.toFile();

        if (!requestFile.exists()) {
            requestPath = Paths.get("static", "user", request.getUrlTail());
            if (!requestPath.toFile().exists())
                return new HttpNotFound();
        }

        try {
            byte[] fileData = Files.readAllBytes(requestPath);

            IHttpResponse response = new HttpOk();
            String contentType = Files.probeContentType(requestPath);

            response.setHeader("Content-Length", String.valueOf(fileData.length));
            response.setHeader("Content-Type", contentType);
            if (loadBody) response.setBody(fileData);
            return response;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    private IHttpResponse handlePutRequest(IHttpRequest request) throws IOException {

        if (request.getUrlTail().equals("/"))
            return new HttpForbidden("You can not PUT on the root");

        if (request.getUrlTail().startsWith("/user"))
            return new HttpForbidden("You can not PUT directly into the user folder");

        Path filePath = Paths.get("static", "user", request.getUrlTail() + ".txt");

        FileOutputStream fos = new FileOutputStream(filePath.toFile());
        fos.write(request.getBody());

        return new HttpCreated(String.format("%s%s", "user", request.getUrlTail()));
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
