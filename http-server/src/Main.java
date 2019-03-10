import application.core.HttpServer;
import application.exceptions.HttpServerException;

public class Main {

    public static void main(String[] args) {
        HttpServer server = new HttpServer();

        try {
            server.setup();
            server.start();
        } catch (HttpServerException e) {
            System.out.println(e.getMessage());
        }
    }
}
