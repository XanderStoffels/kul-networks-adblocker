import application.core.imp.HttpClient;
import application.core.api.IHttpClient;
import application.exceptions.HttpClientConnectionException;
import application.messaging.model.HttpMethod;
import application.messaging.api.IHttpRequest;
import application.messaging.api.IHttpResponse;
import application.messaging.imp.HttpRequest;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {

        IHttpClient client = new HttpClient("www.google.be");
        try {
            client.connect();

            IHttpRequest request = new HttpRequest(HttpMethod.GET);
            request.setHeader("Host", "www.google.be");

            System.out.println(request.toString());
            System.out.println();

            IHttpResponse response = client.request(request);
            String responseAsString = toBeautifulString(response);
           // System.out.println(responseAsString);

            BufferedWriter writer = new BufferedWriter(new FileWriter("./out.html"));
            writer.write(responseAsString);
            writer.flush();
            writer.close();

            List<String> allMatches = new ArrayList<>();
            Matcher m = Pattern.compile("<img\\b[^>]+?src\\s*=\\s*['\"]?([^\\s'\"?#>]+)")
                    .matcher(response.getBody());
            while (m.find()) {
                allMatches.add(m.group());
            }
            allMatches.forEach(s -> {
                String base = (s.split("src=\"")[1]);
                String url = String.format("%s%s", "www.google.com", base);

                IHttpRequest imageRequest = new HttpRequest(HttpMethod.GET);
                imageRequest.setHeader("Host", "www.google.com");
                imageRequest.setUrlTail(base);
                try {
                    IHttpResponse imageResponse = client.request(imageRequest);
                } catch (HttpClientConnectionException e) {
                    System.out.println("Could not load image from " + url);
                    System.out.println(e.getMessage());
                    System.out.println(e.getCause().getMessage());
                }


            });


            System.exit(0);

        } catch (HttpClientConnectionException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCause().getMessage());
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Could not write file to disk");
        }
    }

    private static String toBeautifulString(IHttpResponse response) {
        StringBuilder builder = new StringBuilder();
        builder.append("** STATUS **\n");
        builder.append(String.format("%s %s\n\n",
                response.getResponseStatus().getStatusCode(),
                response.getResponseStatus().getStatusMessage()));

        builder.append("** HEADERS **\n");
        response.getHeaders().stream().sorted().forEach(h -> {
            builder.append(String.format("%-30s : %s\n",h,response.getHeaderValue(h)));
        });
        builder.append("\n");

        if (response.getBody().length() == 0) return builder.toString();
        builder.append("** BODY **\n");
        builder.append(response.getBody());

        return builder.toString();
    }
}
