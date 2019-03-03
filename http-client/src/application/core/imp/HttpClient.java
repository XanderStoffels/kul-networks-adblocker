package application.core.imp;

import application.core.api.IHttpClient;
import application.core.receivers.api.IHttpBodyReceiver;
import application.core.receivers.exceptions.BodyReceiverException;
import application.core.receivers.imp.ContentLengthBodyReceiver;
import application.exceptions.HttpClientConnectionException;
import application.messaging.api.IHttpRequest;
import application.messaging.api.IHttpResponse;
import application.messaging.imp.HttpResponse;
import application.messaging.model.HttpMethod;
import application.messaging.model.ResponseStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.Buffer;
import java.util.*;
import java.util.stream.Collectors;

public class HttpClient implements IHttpClient {

    private Socket socket;
    private String baseUrl;
    private int port;

    public HttpClient(String baseUrl) {
        this(baseUrl, 80);
    }
    public HttpClient(String baseUrl, int port) {
        this.baseUrl = baseUrl;
        this.port = port;
    }

    @Override
    public IHttpResponse request(IHttpRequest request) throws HttpClientConnectionException {
        PrintWriter writer;
        BufferedReader reader;

        try {
            writer = new PrintWriter(socket.getOutputStream());
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            throw new HttpClientConnectionException("Could not open IO stream from socket.", e);
        }

        // Send the request
        sendRequest(request, writer);

        // Receive response status
        ResponseStatus responseStatus = receiveStatus(reader);

        // Receive response headers
        Map<String, String> headers =  receiveHeaders(reader);

        // Receive response body if necessary
        // A response to a HEAD request does not have a body
        // Not every body-response protocol is supported
        try {
            String body = (request.getMethod() == HttpMethod.HEAD) ? "" : receiveBody(reader, headers);
            return new HttpResponse(responseStatus, headers, body);
        } catch (UnsupportedOperationException e) {
            throw new HttpClientConnectionException("Unsupported response protocol", e);
        } catch (BodyReceiverException e) {
            throw new HttpClientConnectionException("Error while receiving response-body from server", e);
        }

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
    public String getBaseUrl() {
        return this.baseUrl;
    }

    @Override
    public int getPort() {
        return this.port;
    }

    private void sendRequest(IHttpRequest request, PrintWriter writer) {
        String requestString = request.toString();
        writer.print(requestString);
        writer.flush();
    }
    private ResponseStatus receiveStatus(BufferedReader reader) throws HttpClientConnectionException {
        try {
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
    private Map<String, String> receiveHeaders(BufferedReader reader) throws HttpClientConnectionException {
        try {
            String i;
            ArrayList<String> headerLines = new ArrayList<>();
            while((i = reader.readLine()) != null) {
                if (i.length() == 0) break;
                headerLines.add(i);
            }

            Map<String, String> headers = new HashMap<>();
            for (String l : headerLines) {
                int index = l.indexOf(':');
                if (index < 0) {
                    System.out.println("Received a malformed header");
                    continue;
                }
                String key = l.substring(0, index).trim();
                String value = l.substring(index + 1).trim();
                headers.put(key, value);
            }
            return headers;

        } catch (IOException e) {
            throw new HttpClientConnectionException("Could not receive headers", e);
        }
    }
    private String receiveBody(BufferedReader reader, Map<String, String> headers) throws UnsupportedOperationException, BodyReceiverException {
        // Check if one of the supported headers are present
        final String ContentLength = "Content-Length";
        final String TransferEncoding = "Transfer-Encoding";

        if (headers.containsKey(ContentLength)) {
            int contentLength = Integer.parseInt(headers.get(ContentLength));
            IHttpBodyReceiver receiver = new ContentLengthBodyReceiver(contentLength);
            return receiver.getBody(reader);
        }

        if (headers.containsKey(TransferEncoding)) {
            return "roo";
        } else {
            throw new UnsupportedOperationException("The server's body-response protocol is not supported");
        }
    }

}
