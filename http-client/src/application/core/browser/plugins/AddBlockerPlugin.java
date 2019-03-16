package application.core.browser.plugins;

import messaging.api.IHttpResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class AddBlockerPlugin {

    public IHttpResponse filter(IHttpResponse response) {
        String htmlString = new String(response.getBody());
        Document doc = Jsoup.parse(htmlString);
        Elements images = doc.getElementsByTag("img");

        images.forEach(image -> {
            if (image.attr("src").contains("ad"))
                image.remove();
        });

        response.setBody(doc.body().toString().getBytes());
        return response;
    }

}
