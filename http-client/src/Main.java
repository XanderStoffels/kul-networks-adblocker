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
            String responseAsString = toBeautifulString(response);
            System.out.println(responseAsString);

            System.exit(0);

        } catch (HttpClientConnectionException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCause().getMessage());
            System.exit(1);
        }
    }

    private static String toBeautifulString(IHttpResponse response) {
        StringBuilder builder = new StringBuilder();
        builder.append("** STATUS **\n");
        builder.append(String.format("%s %s\n\n",
                response.getResponseStatus().getStatusCode(),
                response.getResponseStatus().getStatusMessage()));

        builder.append("** HEADERS **\n");
        response.getHeaders().stream().sorted().forEach(h -> {
            builder.append(String.format("%-30s : %s\n",h,response.getHeaderValue(h)));
        });
        builder.append("\n");

        if (response.getBody().length() == 0) return builder.toString();
        builder.append("** BODY **\n");
        builder.append(response.getBody());

        return builder.toString();
    }
}
