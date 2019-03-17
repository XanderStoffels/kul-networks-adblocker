package application.core.browser.plugins.imp;

import application.core.browser.plugins.api.IWebBrowserPlugin;
import application.core.client.api.IHttpClient;
import messaging.api.IHttpRequest;
import messaging.api.IHttpResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

public class WebpageSaverPlugin implements IWebBrowserPlugin {
    @Override
    public IHttpResponse passThrough(IHttpClient client, IHttpRequest originalRequest, IHttpResponse originalResponse) {
        File outputDirectory = Paths.get("downloads", client.getBaseUrl()).toFile();
        outputDirectory.mkdirs();

        String pageName;
        if (originalRequest.getUrlTail().equals("/"))
            pageName = "index";
        else
            pageName = originalRequest.getUrlTail().replace("/", "_");


        File outputFile = Paths.get(
                "downloads",
                client.getBaseUrl(),
                pageName + ".html")
                .toFile();

        FileOutputStream saveStream;
        try {
            saveStream = new FileOutputStream(outputFile);
            saveStream.write(originalResponse.getBody());
            saveStream.flush();
            saveStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return originalResponse;

    }
}
