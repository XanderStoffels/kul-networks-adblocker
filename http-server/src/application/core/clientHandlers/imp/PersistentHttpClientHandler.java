package application.core.clientHandlers.imp;

import application.core.clientHandlers.api.IHttpClientHandler;
import application.responses.*;
import messaging.api.IHttpRequest;
import messaging.api.IHttpResponse;
import messaging.imp.HttpRequest;
import messaging.model.HttpMethod;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class PersistentHttpClientHandler implements IHttpClientHandler {
    @Override
    public void handle(Socket client) {
        int sessionId = new Random().nextInt();
        System.out.printf("Session created: %x\n", sessionId);
        BufferedReader reader;
        OutputStream writer;

        // Encapsulate streams
        try {
            reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            writer = client.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Start the request loop
        try {
            requestLoop(reader, writer);
            client.close();
        } catch (IOException e) {
            System.out.println();
        }
    }

    private void requestLoop(BufferedReader reader, OutputStream writer) throws IOException {
        boolean keepAlive = true;

        while (keepAlive) {
            // Parse the request
            String sRequest = getRequestString(reader);
            IHttpRequest request = HttpRequest.parse(sRequest);

            keepAlive = "keep-alive".equals(request.getHeaderValue("Connection"));

            if (request.getMethod() != HttpMethod.GET && request.getMethod() != HttpMethod.HEAD) {
                int length = Integer.parseInt(request.getHeaderValue("Content-Length"));

                StringBuilder builder = new StringBuilder();
                while (builder.toString().length() != length)
                    builder.append((char) reader.read());

                request.setBody(builder.toString().getBytes());
            }

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
                response.setHeader("Connection", "keep-alive");

            // Serialize response
            byte[] responseBytes = response.serialize();

            // Write the response to the stream
            writer.write(responseBytes);
            writer.flush();
        }
    }

    private String getRequestString(BufferedReader reader) throws IOException {
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
        return String.join("\r\n", contents);
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
                return new HttpOk();
        }
        return new HttpServerError(String.format("Method %s is not supported", request.getMethod().name()));
    }

    private IHttpResponse handleGetRequest(IHttpRequest request, boolean loadBody) {

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
}
