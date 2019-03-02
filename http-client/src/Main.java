import application.HttpClient;
import application.IHttpClient;
import application.exceptions.HttpClientConnectionException;
import application.messaging.HttpMethod;
import application.messaging.api.IHttpRequest;
import application.messaging.imp.HttpRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {

        IHttpClient client = new HttpClient("www.google.com");
        try {
            client.connect();

            IHttpRequest request = new HttpRequest(HttpMethod.GET);
            request.setHeader("Host", "www.google.com");

            client.send(request);
            System.out.println("Done!");

        } catch (HttpClientConnectionException e) {
            e.printStackTrace();
        }


      /*
        String url = "www.google.be";
        try {
            Socket s = new Socket(InetAddress.getByName(url), 80);
            PrintWriter writer = new PrintWriter(s.getOutputStream());
            StringBuilder builder = new StringBuilder();
            builder.append("GET / HTTP/1.1\r\n");
            builder.append(String.format("Host: %s\r\n", url));
            builder.append("\r\n");

            System.out.println("REQUEST:");
            System.out.println("-----");
            System.out.print(builder.toString());
            System.out.println("-----");

            writer.print(builder.toString());
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            String i;
            ArrayList<String> headerLines = new ArrayList<>();
            while((i = reader.readLine()) != null) {
                if (i.length() == 0) break;
                headerLines.add(i);

            }

            HashMap<String, String> headers = new HashMap<>();
            headers.put("Status", headerLines.get(0));
            headers.put("Date", headerLines.get(1));
            headerLines.remove(0);
            headerLines.remove(0);
            for (String l : headerLines) {
                headers.put(l.split(":")[0].trim(), l.split(":")[1].trim());
            }

            headers.forEach((k, v) -> System.out.printf("%s : %s\n", k, v));
            if (headers.containsKey("Transfer-Encoding")) {
                if (headers.get("Transfer-Encoding").equals("chunked")) {

                }
            }

       } catch (IOException e) {
            System.out.println("An IO error occurred");
            System.out.println(e.getMessage());
            System.exit(1);
       }

       */

    }
}
