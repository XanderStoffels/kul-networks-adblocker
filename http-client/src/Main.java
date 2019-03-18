import application.core.browser.IWebBrowser;
import application.core.browser.WebBrowser;
import application.core.browser.model.HttpLocator;
import application.core.browser.plugins.imp.AddBlockerPlugin;
import application.core.browser.plugins.imp.ImageSaverPlugin;
import application.core.browser.plugins.imp.WebpageSaverPlugin;
import messaging.api.IHttpResponse;
import messaging.model.HttpMethod;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Wrong amount of arguments!");
            System.exit(1);
        }

        HttpMethod verb = null;
        try {
             verb = HttpMethod.valueOf(args[0]);
        } catch (IllegalArgumentException ex) {
            System.out.println("Unknown HTTP verb");
            System.exit(1);
        }

        final String uri = args[1];
        if (!StringUtils.isNumeric(args[2])) {
            System.out.println("The port has to be a numeric value!");
            System.exit(1);
        }
        final int port = Integer.parseInt(args[2]);
        final HttpLocator httpLocator = HttpLocator.parse(uri, port);

        IWebBrowser webBrowser = new WebBrowser();
        webBrowser.loadPlugin(new AddBlockerPlugin());
        webBrowser.loadPlugin(new WebpageSaverPlugin());
        webBrowser.loadPlugin(new ImageSaverPlugin());

        IHttpResponse response = null;
        Scanner scanner = new Scanner(System.in);
        String body;
        switch (verb) {
            case HEAD:
                response = webBrowser.head(httpLocator);
                break;
            case GET:
                response = webBrowser.get(httpLocator);
                break;
            case POST:
                System.out.println("Please provide a body:");
                body = scanner.nextLine();
                response = webBrowser.post(httpLocator, body.getBytes());
                break;
            case PUT:
                System.out.println("Please provide a body:");
                body = scanner.nextLine();
                response = webBrowser.put(httpLocator, body.getBytes());
                break;
        }

        printResponse(response);
    }

    private static void printResponse(IHttpResponse response){
        StringBuilder builder = new StringBuilder();
        builder.append("** STATUS **\n");
        builder.append(String.format("%s\n", response.getHttpVersion()));
        builder.append(String.format("%s %s\n\n",
                response.getResponseStatus().getStatusCode(), response.getResponseStatus().getStatusMessage()));

        builder.append("** HEADERS **\n");
        for (Map.Entry<String, String> header : response.getHeaders().getValues().entrySet()) {
            builder.append(String.format("%-20s: %s\n", header.getKey(), header.getValue()));
        }
        builder.append("\n");
        builder.append("** BODY **\n");
        builder.append(new String(response.getBody()));

        System.out.println(builder.toString());
    }

}
