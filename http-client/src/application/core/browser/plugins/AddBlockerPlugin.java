package application.core.browser.plugins;

import application.core.browser.plugins.api.IWebBrowserPlugin;
import application.core.client.api.IHttpClient;
import messaging.api.IHttpRequest;
import messaging.api.IHttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class AddBlockerPlugin implements IWebBrowserPlugin {
    @Override
    public IHttpResponse passThrough(IHttpClient client, IHttpRequest originalRequest, IHttpResponse originalResponse) {
        String htmlString = new String(originalResponse.getBody());
        Document doc = Jsoup.parse(htmlString);
        Elements images = doc.getElementsByTag("img");

        images.forEach(image -> {
            if (image.attr("src").contains("ad"))
                image.remove();
        });

        originalResponse.setBody(doc.body().toString().getBytes());
        return originalResponse;
    }
}
