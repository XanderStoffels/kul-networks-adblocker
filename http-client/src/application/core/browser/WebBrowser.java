package application.core.browser;

import application.core.browser.model.HttpLocator;
import application.core.browser.plugins.api.IWebBrowserPlugin;
import application.core.client.api.IHttpClient;
import application.core.client.imp.HttpClient;
import application.exceptions.HttpClientException;
import messaging.api.IHttpRequest;
import messaging.api.IHttpResponse;
import messaging.imp.HttpRequest;
import messaging.model.HttpMethod;

import java.util.ArrayList;
import java.util.List;

public class WebBrowser implements IWebBrowser {

    private List<IWebBrowserPlugin> plugins;

    public WebBrowser() {
        this.plugins = new ArrayList<>();
    }

    @Override
    public IHttpResponse get(HttpLocator locator) {
        IHttpClient client = new HttpClient(locator.getHost(), locator.getPort());
        IHttpRequest request = new HttpRequest(HttpMethod.GET);

        request.setUrlTail(locator.getTail());
        request.setHttpVersion("HTTP/1.1");
        request.getHeaders().set("Host", locator.getHost());
        request.getHeaders().set("Connection", "keep-alive");

        IHttpResponse response = null;
        try {
            client.connect();
            response = client.request(request);
        } catch (HttpClientException e) {
            e.printStackTrace();
        }

        response = this.putThroughPlugins(client, request, response);
        return response;
    }
    @Override
    public IHttpResponse post(HttpLocator locator, byte[] body) {
        IHttpClient client = new HttpClient(locator.getHost(), locator.getPort());
        IHttpRequest request = new HttpRequest(HttpMethod.POST);

        request.setUrlTail(locator.getTail());
        request.setHttpVersion("HTTP/1.1");
        request.getHeaders().set("Host", locator.getHost());
        request.getHeaders().set("Connection", "keep-alive");
        request.getHeaders().set("Content-Length", String.valueOf(body.length));
        request.getHeaders().set("Content-Type", "text/plain");
        request.setBody(body);

        IHttpResponse response = null;
        try {
            client.connect();
            response = client.request(request);
        } catch (HttpClientException e) {
            e.printStackTrace();
        }

        response = this.putThroughPlugins(client, request, response);
        return response;
    }
    @Override
    public IHttpResponse put(HttpLocator locator, byte[] body) {
        IHttpClient client = new HttpClient(locator.getHost(), locator.getPort());
        IHttpRequest request = new HttpRequest(HttpMethod.PUT);

        request.setUrlTail(locator.getTail());
        request.setHttpVersion("HTTP/1.1");
        request.getHeaders().set("Host", locator.getHost());
        request.getHeaders().set("Connection", "keep-alive");
        request.getHeaders().set("Content-Length", String.valueOf(body.length));
        request.getHeaders().set("Content-Type", "text/plain");
        request.setBody(body);

        IHttpResponse response = null;
        try {
            client.connect();
            response = client.request(request);
        } catch (HttpClientException e) {
            e.printStackTrace();
        }

        response = this.putThroughPlugins(client, request, response);
        return response;
    }
    @Override
    public IHttpResponse head(HttpLocator locator) {
        IHttpClient client = new HttpClient(locator.getHost(), locator.getPort());
        IHttpRequest request = new HttpRequest(HttpMethod.HEAD);

        request.setUrlTail(locator.getTail());
        request.setHttpVersion("HTTP/1.1");
        request.getHeaders().set("Host", locator.getHost());
        request.getHeaders().set("Connection", "keep-alive");

        IHttpResponse response = null;
        try {
            client.connect();
            response = client.request(request);
        } catch (HttpClientException e) {
            e.printStackTrace();
        }

        response = this.putThroughPlugins(client, request, response);
        return response;
    }

    @Override
    public void loadPlugin(IWebBrowserPlugin plugin) {
        this.plugins.add(plugin);
    }

    private IHttpResponse putThroughPlugins(IHttpClient client,  IHttpRequest request, IHttpResponse response) {
        for (IWebBrowserPlugin plugin : this.plugins) {
            response  = plugin.passThrough(client, request, response);
        }
        return response;
    }
}
