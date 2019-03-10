package application.core.browser;

import application.core.browser.model.UrlParts;
import application.core.client.api.IHttpClient;
import application.core.client.imp.HttpClient;
import application.exceptions.HttpClientException;
import messaging.api.IHttpRequest;
import messaging.api.IHttpResponse;
import messaging.imp.HttpRequest;
import messaging.model.HttpMethod;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

public class WebBrowser {

    private int port;
    private File saveFolder;

    public WebBrowser(int port) {
        this.port = port;
        this.saveFolder = new File("browser");
        this.saveFolder.mkdir();
    }

    public void get(String url) {
        UrlParts urlParts = UrlParts.parse(url);

        IHttpClient client = new HttpClient(urlParts.getHost(), port);
        IHttpRequest request = new HttpRequest(HttpMethod.GET);
        request.setUrlTail(urlParts.getTail());
        request.getHeaders().set("Host", urlParts.getHost());

        IHttpResponse response;
        try {
            response = client.request(request);
        } catch (HttpClientException e) {
            e.printStackTrace();
            return;
        }

        // Save to HTML file
        saveResponseFrom(urlParts, response);

        // Load images, if any
        saveImagesInResponse(urlParts, response);
    }

    private void saveImagesInResponse(UrlParts urlParts, IHttpResponse response) {
        if (!response.getHeaders().getOrEmpty("Content-Type").startsWith("text/html")) return;
        

    }

    private void saveResponseFrom(UrlParts urlParts, IHttpResponse response) {
        urlParts = new UrlParts(urlParts.getHost().replace("/", "_")
                , urlParts.getTail().replace("/", "_"));

        if (urlParts.getTail().equals("_"))
            urlParts.setTail("index");

        File urlDir = Paths.get(this.saveFolder.getPath(), urlParts.getHost()).toFile();
        urlDir.mkdir();

        File resultFile = Paths.get(urlDir.getPath(), urlParts.getTail() + ".html").toFile();
        try (FileOutputStream output = new FileOutputStream(resultFile)){
            output.write(response.getBody());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
