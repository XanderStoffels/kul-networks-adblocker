import application.core.client.api.IHttpClient;
import application.core.client.imp.HttpClient;
import application.exceptions.HttpClientException;
import messaging.api.IHttpRequest;
import messaging.api.IHttpResponse;
import messaging.imp.HttpRequest;
import messaging.model.HttpMethod;
import org.apache.commons.lang3.StringUtils;

public class Main {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Wrong amount of arguments!");
            System.exit(1);
        }

        final String verb = args[0];
        final String uri = args[1];

        final String tail;
        final String baseUrl;
        final String[] uriParts = uri.split("/", 2);
        if (uriParts.length != 2) {
            tail = "/";
        } else {
            tail = "/" + uriParts[1];
        }
        baseUrl = uriParts[0];

        if (!StringUtils.isNumeric(args[2])) {
            System.out.println("The port has to be a numeric value!");
            System.exit(1);
        }
        final int port = Integer.parseInt(args[2]);

        HttpMethod method = HttpMethod.valueOf(verb.toUpperCase());

        IHttpRequest request = new HttpRequest(method);
        request.setUrlTail(tail);

        IHttpClient client = new HttpClient(baseUrl, port);
        try {
            IHttpResponse response = client.request(request);
            System.out.println(new String(response.getBody()));
        } catch (HttpClientException e) {
            e.printStackTrace();
        }


    }
}
