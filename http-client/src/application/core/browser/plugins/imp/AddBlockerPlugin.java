package application.core.browser.plugins.imp;

import application.core.browser.plugins.api.IWebBrowserPlugin;
import application.core.client.api.IHttpClient;
import messaging.api.IHttpRequest;
import messaging.api.IHttpResponse;
import messaging.model.HttpMethod;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class AddBlockerPlugin implements IWebBrowserPlugin {
    @Override
    public IHttpResponse passThrough(IHttpClient client, IHttpRequest originalRequest, IHttpResponse originalResponse) {

        if (originalRequest.getMethod() != HttpMethod.GET) return originalResponse;

        String htmlString = new String(originalResponse.getBody());
        Document doc = Jsoup.parse(htmlString);
        Elements images = doc.getElementsByTag("img");

        images.forEach(image -> {
            if (image.attr("src").contains("ad"))
                image.remove();
        });

        if (images.size() != 0)
            originalResponse.setBody(doc.html().getBytes());
        return originalResponse;
    }
}
