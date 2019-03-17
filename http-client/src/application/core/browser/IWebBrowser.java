package application.core.browser;

import application.core.browser.model.HttpLocator;
import application.core.browser.plugins.api.IWebBrowserPlugin;
import messaging.api.IHttpResponse;

public interface IWebBrowser {
    IHttpResponse get(HttpLocator url);
    IHttpResponse post(HttpLocator url, byte[] body);
    IHttpResponse put(HttpLocator url, byte[] body);
    IHttpResponse head(HttpLocator url);

    void loadPlugin(IWebBrowserPlugin plugin);
}
