import application.core.api.IHttpClient;
import application.core.imp.HttpClient;
import application.exceptions.HttpClientConnectionException;
import messaging.api.IHttpRequest;
import messaging.api.IHttpResponse;
import messaging.imp.HttpRequest;
import messaging.model.HttpMethod;

public class Main {

    public static void main(String[] args) {

        final String url = "www.example.com";
        IHttpClient client = new HttpClient(url);
        try {
            client.connect();

            IHttpRequest request = new HttpRequest(HttpMethod.GET);
            request.setUrlTail("/");
            //request.setUrlTail("/ad1.jpg");
            request.setHeader("Host", url);

            System.out.println(request.toString());

            IHttpResponse response = client.request(request);
            System.out.println(toBeautifulString(response));

            /*
            String responseAsString = toBeautifulString(response);

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
                    IHttpResponse imageResponse = client.htmlRequest(imageRequest);
                    //saveBeautifulImage(imageResponse);
                } catch (HttpClientConnectionException e) {
                    System.out.println("Could not load image from " + url);
                    System.out.println(e.getMessage());
                    System.out.println(e.getCause().getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            System.exit(0);

            */

        } catch (HttpClientConnectionException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCause().getMessage());
            System.exit(1);
      //  } catch (IOException e) {
      //      System.out.println("Could not write file to disk");
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
            builder.append(String.format("%-30s : %s\n", h, response.getHeaderValue(h)));
        });
        builder.append("\n");

        if (response.getBody().length == 0) return builder.toString();
        builder.append("** BODY **\n");
        builder.append(new String(response.getBody()));

        return builder.toString();
    }
}
