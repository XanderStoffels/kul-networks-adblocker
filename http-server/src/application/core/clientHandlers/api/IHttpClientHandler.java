package application.core.clientHandlers.api;

import java.net.Socket;

public interface IHttpClientHandler {
    void handle(Socket client);
}
