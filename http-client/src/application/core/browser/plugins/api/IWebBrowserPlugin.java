package application.core.browser.plugins.api;

import application.core.client.api.IHttpClient;
import messaging.api.IHttpRequest;
import messaging.api.IHttpResponse;

public interface IWebBrowserPlugin {
    IHttpResponse passThrough(IHttpClient client, IHttpRequest originalRequest, IHttpResponse originalResponse);
}
