import application.core.browser.IWebBrowser;
import application.core.browser.WebBrowser;
import application.core.browser.model.HttpLocator;
import application.core.browser.plugins.AddBlockerPlugin;
import application.core.browser.plugins.ImageDownloaderPlugin;
import messaging.api.IHttpResponse;
import org.apache.commons.lang3.StringUtils;

public class Main {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Wrong amount of arguments!");
            System.exit(1);
        }

        final String verb = args[0];
        final String uri = args[1];

        if (!StringUtils.isNumeric(args[2])) {
            System.out.println("The port has to be a numeric value!");
            System.exit(1);
        }
        final int port = Integer.parseInt(args[2]);
        final HttpLocator httpLocator = HttpLocator.parse(uri, port);

        IWebBrowser webBrowser = new WebBrowser();
        webBrowser.loadPlugin(new AddBlockerPlugin());
        webBrowser.loadPlugin(new ImageDownloaderPlugin());

        IHttpResponse response = webBrowser.get(httpLocator);
        System.out.println(new String(response.getBody()));
    }

}
