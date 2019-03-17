package application.core.browser.model;

import java.util.Arrays;
import java.util.stream.Collectors;

public class HttpLocator {
    private String host;
    private String tail;
    private int port;

    public HttpLocator(String host, String tail, int port) {
        this.host = host;
        this.tail = tail;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public String getTail() {
        return tail;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setTail(String tail) {
        this.tail = tail;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public static HttpLocator parse(String line, int port) {
        String host;
        String tail = "/";
        if (line.contains("/")){
            String[] urlParts = line.split("/");
            if (urlParts.length > 1) {
                host = urlParts[0];
                tail += Arrays.stream(urlParts).skip(1).collect(Collectors.joining("/"));
            } else {
                host = line.substring(0, line.length() -1);
            }
        } else {
            host = line;
        }
        return new HttpLocator(host, tail, port);
    }
}
