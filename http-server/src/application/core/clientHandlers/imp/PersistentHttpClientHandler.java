package application.core.clientHandlers.imp;

import application.core.clientHandlers.api.IHttpClientHandler;
import application.responses.*;
import messaging.api.IHttpRequest;
import messaging.api.IHttpResponse;
import messaging.imp.BaseHttpMessage;
import messaging.imp.HttpRequest;
import messaging.model.HttpMethod;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.sql.*;

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
            System.out.println(e.getMessage());
            return;
        }

        // Start the request loop
        try {
            requestLoop(reader, writer);
            client.close();

        }catch (SocketException e) {
            // Ignored, the client disconnected
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void requestLoop(BufferedReader reader, OutputStream writer) throws IOException {
        boolean keepAlive = true;

        while (keepAlive) {

            // Parse the request headers
            String sRequest = getRequestString(reader);
            IHttpRequest request = HttpRequest.parse(sRequest);

            keepAlive = request.getHttpVersion().equals("HTTP/1.1");
            if (keepAlive && request.getHeaders().getOrEmpty("Connection").equals("close"))
                keepAlive = false;

            // Check for Host header
            if (!request.getHeaders().hasHeader("Host")){
                writer.write(new HttpBadRequest("Missing Host header").serialize());
                writer.flush();
                continue;
            }

            // Read the body if necessary
            if (request.getMethod() != HttpMethod.GET && request.getMethod() != HttpMethod.HEAD) {
                if (!request.getHeaders().hasHeader("Content-Length")){
                    writer.write(new HttpBadRequest("Missing Content-Length header").serialize());
                    writer.flush();
                    continue;
                }
                int length = Integer.parseInt(request.getHeaders().getOrEmpty("Content-Length"));

                StringBuilder builder = new StringBuilder();
                while (builder.toString().length() != length)
                    builder.append((char) reader.read());

                request.setBody(builder.toString().getBytes());
            }

            // Generate response
            IHttpResponse response;

            try {
                response = this.handleHttpMethod(request);
            } catch (Exception e) {
                response = new HttpServerError(String.format("%s\n%s", "An internal server error occurred", e.getMessage()));
            }

            response.setHttpVersion(BaseHttpMessage.standardHttpVersion);
            response.getHeaders().set("Date", new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));

            if (!response.getHeaders().hasHeader("Content-Length"))
                response.getHeaders().set("Content-Length", String.valueOf(response.getBody().length));

            if (keepAlive)
                response.getHeaders().set("Connection", "keep-alive");

            // Serialize response
            byte[] responseBytes = response.serialize();

            // Write the response to the stream
            writer.write(responseBytes);
            writer.flush();
        }
    }

    private String getRequestString(BufferedReader reader) throws IOException {
        // Read the headline and headers
        boolean contentAvailable = true;
        List<String> contents = new ArrayList<>();

        // Read incoming request
        while (contentAvailable) {
            String content = reader.readLine();
            if (content == null || content.length() == 0) {
                contentAvailable = false;
            } else {
                contents.add(content);
            }
        }
        return String.join("\r\n", contents);
    }

    private IHttpResponse handleHttpMethod(IHttpRequest request) throws IOException {
        switch (request.getMethod()) {
            case HEAD:
                return handleGetRequest(request, false);
            case GET:
                return handleGetRequest(request, true);
            case PUT:
                return handlePutRequest(request);
            case POST:
                return handlePostRequest(request);
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

        if (checkIfModifiedSince(request, requestFile)) return new HttpNotModified();

        try {
            byte[] fileData = Files.readAllBytes(requestPath);

            IHttpResponse response = new HttpOk();
            String contentType = Files.probeContentType(requestPath);

            response.getHeaders().set("Content-Length", String.valueOf(fileData.length));
            response.getHeaders().set("Content-Type", contentType);
            if (loadBody) response.setBody(fileData);
            return response;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private IHttpResponse handlePostRequest(IHttpRequest request) throws IOException {
        if (request.getUrlTail().equals("/"))
            return new HttpForbidden("You can not POST on the root");

        if (request.getUrlTail().startsWith("/user"))
            return new HttpForbidden("You can not POST directly into the user folder");

        Paths.get("static", "user").toFile().mkdir();
        Path filePath = Paths.get("static", "user", request.getUrlTail() + ".txt");

        boolean created = false;
        if (!filePath.toFile().exists()) {
            filePath.toFile().createNewFile();
            created = true;

        }

        FileOutputStream fos = new FileOutputStream(filePath.toFile(), true);
        fos.write(request.getBody());
        fos.flush();
        fos.close();

        if (created)
            return new HttpCreated(String.format("%s%s", "user", request.getUrlTail() + ".txt"));

        return new HttpOk();
    }
    private IHttpResponse handlePutRequest(IHttpRequest request) throws IOException {

        if (request.getUrlTail().equals("/"))
            return new HttpForbidden("You can not PUT on the root");

        if (request.getUrlTail().startsWith("/user"))
            return new HttpForbidden("You can not PUT directly into the user folder");

        Paths.get("static", "user").toFile().mkdir();
        Path filePath = Paths.get("static", "user", request.getUrlTail() + ".txt");

        FileOutputStream fos = new FileOutputStream(filePath.toFile());
        fos.write(request.getBody());
        fos.flush();
        fos.close();

        return new HttpCreated(String.format("%s%s", "user", request.getUrlTail() + ".txt"));
    }

    private boolean checkIfModifiedSince(IHttpRequest request, File requestFile) {
        if (request.getHeaders().hasHeader("If-Modified-Since")) {
            Date fileDate = new Date(requestFile.lastModified());
            Date headerDate = Timestamp.valueOf(LocalDateTime.parse(request.getHeaders().getOrEmpty("If-Modified-Since"),
                    DateTimeFormatter.RFC_1123_DATE_TIME)) ;

            return (fileDate.before(headerDate));
        }
        return false;
    }
}