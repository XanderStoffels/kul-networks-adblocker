package application.messaging.api;

import application.messaging.model.HttpMethod;

public interface IHttpRequest extends IHttpMessage{

    void setMethod(HttpMethod method);
    HttpMethod getMethod();

    void setUrlTail(String tail);
    String getUrlTail();

}
