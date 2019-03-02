package application.core.imp;

import application.core.api.IHttpClient;
import application.exceptions.HttpClientConnectionException;
import application.messaging.api.IHttpRequest;
import application.messaging.api.IHttpResponse;
import application.messaging.imp.HttpResponse;
import application.messaging.model.ResponseStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

public class HttpClient implements IHttpClient {

    private Socket socket;
    private String baseUrl;
    private int port;
    public static final String httpVersion = "HTTP/1.1";

    public HttpClient(String baseUrl) {
        this(baseUrl, 80);
    }

    public HttpClient(String baseUrl, int port) {
        this.baseUrl = baseUrl;
        this.port = port;
    }

    @Override
    public void connect() throws HttpClientConnectionException {
        try {
            this.socket = new Socket(baseUrl, port);
        } catch (IOException e) {
            throw new HttpClientConnectionException("Could not connect to remote host", e);
        }
    }

    @Override
    public void disconnect() throws HttpClientConnectionException {
        if (this.socket == null || this.socket.isClosed()) return;
        try {
            this.socket.close();
        } catch (IOException e) {
            throw new HttpClientConnectionException("Could not disconnect from remote host", e);
        }
    }

    @Override
    public IHttpResponse request(IHttpRequest request) throws HttpClientConnectionException {
        String requestString = buildRequestString(request);

        try {
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            writer.print(requestString);
            writer.flush();
        } catch (IOException e) {
            throw new HttpClientConnectionException("Could not request request", e);
        }

        ResponseStatus reponseStatus = receiveStatus();

        IHttpResponse response = new HttpResponse(reponseStatus);

        return response;
    }

    private String buildRequestString(IHttpRequest request) {
        StringBuilder builder = new StringBuilder();

        // Set first line of the request
        builder.append(String.format("%s %s %s\r\n",
                request.getMethod().name(),
                request.getUrlTail(),
                httpVersion));

        // Set headers of the request
        request.getHeaders().forEach(h -> {
            builder.append(String.format("%s : %s\r\n", h, request.getHeaderValue(h)));
        });

        // End with an empty line
        builder.append("\r\n");

        return builder.toString();
    }

    private ResponseStatus receiveStatus() throws HttpClientConnectionException {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String line = reader.readLine();
            String[] parts = line.split(" ");

            ResponseStatus status = new ResponseStatus(Integer.parseInt(parts[1]));
            status.setHttpVersion(line.split(" ")[0]);
            status.setStatusMessage(Arrays.stream(parts).skip(2).collect(Collectors.joining()));

            return status;
        } catch (IOException e) {
            throw new HttpClientConnectionException("Could not receive Response Status", e);
        }

    }
    private Map<String, String> receiveHeaders() throws HttpClientConnectionException {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String i;
            ArrayList<String> headerLines = new ArrayList<>();
            while((i = reader.readLine()) != null) {
                if (i.length() == 0) break;
                headerLines.add(i);
            }

            Map<String, String> headers = new HashMap<>();
            for (String l : headerLines) {
                String[] parts = l.split(":");
                if (parts.length != 2) {
                    System.out.println("Received a malformed header!");
                    continue;
                }
                headers.put(parts[0], parts[1]);
            }
            return headers;

        } catch (IOException e) {
            throw new HttpClientConnectionException("Could not receive headers", e);
        }
    }

    @Override
    public String getBaseUrl() {
        return this.baseUrl;
    }

    @Override
    public int getPort() {
        return this.port;
    }


}
