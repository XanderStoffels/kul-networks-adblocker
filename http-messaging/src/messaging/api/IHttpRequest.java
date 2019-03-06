package messaging.api;


import messaging.model.HttpMethod;

public interface IHttpRequest extends IHttpMessage{

    void setMethod(HttpMethod method);
    HttpMethod getMethod();

    void setUrlTail(String tail);
    String getUrlTail();

}
