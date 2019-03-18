package application.core.browser.plugins.imp;

import application.core.browser.plugins.api.IWebBrowserPlugin;
import application.core.client.api.IHttpClient;
import application.exceptions.HttpClientException;
import messaging.api.IHttpRequest;
import messaging.api.IHttpResponse;
import messaging.imp.HttpRequest;
import messaging.model.HttpMethod;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

public class ImageSaverPlugin implements IWebBrowserPlugin {
    @Override
    public IHttpResponse passThrough(IHttpClient client,  IHttpRequest originalRequest, IHttpResponse originalResponse) {
        String htmlString = new String(originalResponse.getBody());
        Document doc = Jsoup.parse(htmlString);
        Elements images = doc.getElementsByTag("img");

        images.forEach(image -> makeImageRequest(client, originalRequest, image));
        return originalResponse;
    }

    private void makeImageRequest(IHttpClient client, IHttpRequest originalRequest, Element image) {
        String source = image.attr("src");

        IHttpRequest imageRequest = new HttpRequest(HttpMethod.GET);
        imageRequest.setUrlTail("/" + source);

        imageRequest.getHeaders().set("Host", originalRequest.getHeaders().getOrEmpty("Host"));
        imageRequest.getHeaders().set("Connection", originalRequest.getHeaders().getOrEmpty("Connection"));

        try {
            IHttpResponse imageResponse = client.request(imageRequest);
            File outputDirectory = Paths.get("downloads", client.getBaseUrl()).toFile();
            outputDirectory.mkdirs();

            if (source.contains("/")) {
                int index = source.lastIndexOf("/");
                String pathToCreate = source.substring(0, index);
                source = source.substring(index);
                pathToCreate = pathToCreate.replace(":", "_");
                Paths.get("downloads", client.getBaseUrl(), pathToCreate).toFile().mkdirs();
            }

            File outputFile = Paths.get(
                    "downloads",
                    client.getBaseUrl(),
                    source)
                    .toFile();

            FileOutputStream imageSaveSteam = new FileOutputStream(outputFile);
            imageSaveSteam.write(imageResponse.getBody());
            imageSaveSteam.flush();
            imageSaveSteam.close();

        } catch (HttpClientException | IOException e) {
            e.printStackTrace();
        }
    }

}
