import application.HttpClient;
import application.IHttpClient;
import application.exceptions.HttpClientConnectionException;
import application.messaging.HttpMethod;
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
            System.out.println(response.getStatusMessage());
            System.out.println("Done!");

        } catch (HttpClientConnectionException e) {
            e.printStackTrace();
        }




    }
}
