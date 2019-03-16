package application.core.client.imp;

import application.core.client.api.IHttpClient;
import application.core.client.receivers.api.IHttpBodyReceiver;
import application.core.client.receivers.exceptions.BodyReceiverException;
import application.core.client.receivers.imp.ChunkedBodyReceiver;
import application.core.client.receivers.imp.ContentLengthBodyReceiver;
import application.exceptions.HttpClientException;
import messaging.api.IHttpHeaders;
import messaging.api.IHttpRequest;
import messaging.api.IHttpResponse;
import messaging.imp.HttpResponse;
import messaging.model.HttpHeaders;
import messaging.model.HttpMethod;
import messaging.model.ResponseStatus;

import java.io.*;
import java.net.Socket;
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
    public IHttpResponse request(IHttpRequest request) throws HttpClientException {
        if (socket == null)
            connect();

        BufferedInputStream reader;
        OutputStream writer;

        try {
            writer = socket.getOutputStream();
            reader = new BufferedInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new HttpClientException("Could not open IO stream from socket.", e);
        }

        // Send the request
        sendRequest(request, writer);

        // Receive response status
        ResponseStatus responseStatus = receiveStatus(reader);

        // Receive response headers
        IHttpHeaders headers = receiveHeaders(reader);

        // Receive response body if necessary
        // A response to a HEAD request does not have a body
        // Not every body-response protocol is supported
        try {
            byte[] body = (request.getMethod() == HttpMethod.HEAD) ? new byte[] {} : receiveBody(reader, headers);
            return new HttpResponse(responseStatus, headers, body);
        } catch (UnsupportedOperationException e) {
            throw new HttpClientException("Unsupported response protocol", e);
        } catch (BodyReceiverException e) {
            throw new HttpClientException("Error while receiving response-body from server", e);
        }
    }


    @Override
    public void connect() throws HttpClientException {
        try {
            this.socket = new Socket(baseUrl, port);
        } catch (IOException e) {
            throw new HttpClientException("Could not connect to remote host", e);
        }
    }

    @Override
    public void disconnect() throws HttpClientException {
        if (this.socket == null || this.socket.isClosed()) return;
        try {
            this.socket.close();
        } catch (IOException e) {
            throw new HttpClientException("Could not disconnect from remote host", e);
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

    private void sendRequest(IHttpRequest request, OutputStream writer) throws HttpClientException {
        byte[] requestString = request.serialize();
        try {
            writer.write(requestString);
            writer.flush();
        } catch (IOException e) {
            throw new HttpClientException("Could not send request");
        }
    }

    private ResponseStatus receiveStatus(BufferedInputStream reader) throws HttpClientException {
        final char delimiter = '\n';
        StringBuilder builder = new StringBuilder();

        try {
            while (true) {
                char c = (char)reader.read();
                if (c == delimiter) break;
                builder.append(c);
            }
        } catch (IOException e) {
            throw new HttpClientException("Could not receive Response Status", e);
        }

        String[] parts = builder.toString().split(" ");
        return new ResponseStatus(Integer.parseInt(parts[1]),
                Arrays.stream(parts).skip(2).collect(Collectors.joining(" ")));
    }

    private IHttpHeaders receiveHeaders(BufferedInputStream reader) throws HttpClientException {
        final String delimiter = "\r\n\r\n";
        StringBuilder builder = new StringBuilder();
        try {
            while (!(builder.toString().endsWith(delimiter))) {
                char c = (char)reader.read();
                builder.append(c);
            }

        } catch (IOException e) {
            throw new HttpClientException("Could not receive headers", e);
        }

        String[] headerlines = builder.toString().split("\r\n");
        IHttpHeaders headers = new HttpHeaders();
        for (String l : headerlines) {
            int index = l.indexOf(':');
            if (index < 0) {
                System.out.println("Received a malformed header");
                continue;
            }
            String key = l.substring(0, index).trim();
            String value = l.substring(index + 1).trim();
            headers.set(key, value);
        }
        return headers;
    }

    private byte[] receiveBody(BufferedInputStream reader, IHttpHeaders headers) throws UnsupportedOperationException, BodyReceiverException {
        // Check if one of the supported headers are present
        final String ContentLength = "Content-Length";
        final String TransferEncoding = "Transfer-Encoding";

        if (headers.hasHeader(ContentLength)) {
            int contentLength = Integer.parseInt(headers.getOrEmpty(ContentLength));
            IHttpBodyReceiver receiver = new ContentLengthBodyReceiver(contentLength);
            return receiver.getBody(reader, headers);
        }

        if (headers.hasHeader(TransferEncoding)) {
            if (headers.getOrEmpty(TransferEncoding).equals("chunked")) {
                IHttpBodyReceiver receiver = new ChunkedBodyReceiver();
                return receiver.getBody(reader, headers);
            }
            throw new UnsupportedOperationException(String.format("Unknown protocol for %s, %s", TransferEncoding,
                    headers.getOrEmpty(TransferEncoding)));
        } else {
            throw new UnsupportedOperationException("The server's body-response protocol is not supported");
        }
    }

}
