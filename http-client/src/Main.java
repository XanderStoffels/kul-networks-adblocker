import application.core.imp.HttpClient;
import application.core.api.IHttpClient;
import application.exceptions.HttpClientConnectionException;
import application.messaging.model.HttpMethod;
import application.messaging.api.IHttpRequest;
import application.messaging.api.IHttpResponse;
import application.messaging.imp.HttpRequest;

public class Main {
    public static void main(String[] args) {
        IHttpClient client = new HttpClient("www.linkedin.com");
        try {
            client.connect();

            IHttpRequest request = new HttpRequest(HttpMethod.HEAD);
            request.setHeader("Host", "www.linkedin.com");

            IHttpResponse response = client.request(request);

            if (!response.getResponseStatus().isSuccessful()) {
                System.out.println(response.getResponseStatus().getStatusCode());
                System.out.println(response.getResponseStatus().getStatusMessage());
            }

            response.getHeaders().forEach(h -> {
                System.out.println(String.format("%s : %s",h,response.getHeaderValue(h)));
            });
            System.exit(0);

        } catch (HttpClientConnectionException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCause().getMessage());
            System.exit(1);
        }
    }
}
