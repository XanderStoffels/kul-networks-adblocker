package application.core.browser.model;

import java.util.Arrays;
import java.util.stream.Collectors;

public class UrlParts {
    private String host;
    private String tail;

    public UrlParts(String host, String tail) {
        this.host = host;
        this.tail = tail;
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

    public static UrlParts parse(String line) {
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
        return new UrlParts(host, tail);
    }
}
