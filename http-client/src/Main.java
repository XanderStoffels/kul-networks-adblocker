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

import java.io.FileOutputStream;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        IHttpClient client = new HttpClient("localhost");
        IHttpRequest request = new HttpRequest(HttpMethod.GET);
        request.getHeaders().set("Host", "localhost");
        request.setUrlTail("/ad1.jpg");


        try {
            IHttpResponse response = client.request(request);
            response.getHeaders().getValues().forEach((key, value) -> {
                System.out.printf("%s: %s\n", key, value);
            });

            System.out.println(response.getBody().length);

            FileOutputStream out = new FileOutputStream("C:\\Users\\xande\\Desktop\\out.png");
            out.write(response.getBody());
            out.flush();
            out.close();

        } catch (HttpClientException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
