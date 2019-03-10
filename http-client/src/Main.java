import application.core.browser.WebBrowser;
import application.core.client.api.IHttpClient;
import application.core.client.imp.HttpClient;
import application.exceptions.HttpClientException;
import application.helpers.HttpHelper;
import messaging.api.IHttpRequest;
import messaging.api.IHttpResponse;
import messaging.imp.HttpRequest;
import messaging.imp.HttpResponse;
import messaging.model.HttpMethod;

public class Main {

    public static void main(String[] args) {

        WebBrowser browser = new WebBrowser(80);
        browser.get("localhost");
        System.out.println("Done!");

        IHttpClient client = new HttpClient("www.google.com");


        String x = "Hello world!";
        IHttpRequest request = new HttpRequest(HttpMethod.PUT);
        request.setBody(x.getBytes());

    }

}
