import application.core.imp.HttpClient;
import application.core.api.IHttpClient;
import application.exceptions.HttpClientConnectionException;
import application.messaging.model.HttpMethod;
import application.messaging.api.IHttpRequest;
import application.messaging.api.IHttpResponse;
import application.messaging.imp.HttpRequest;

public class Main {
    public static void main(String[] args) {

        IHttpClient client = new HttpClient("www.google.com");
        try {
            client.connect();

            IHttpRequest request = new HttpRequest(HttpMethod.GET);
            request.setHeader("Host", "www.google.com");

            IHttpResponse response = client.request(request);
            System.out.println(response.getResponseStatus().isSuccessful());
            System.out.println("Done!");
            System.exit(0);

        } catch (HttpClientConnectionException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCause().getMessage());
            System.exit(1);
        }

    }
}
