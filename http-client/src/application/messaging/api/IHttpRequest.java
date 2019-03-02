package application.messaging.api;

import application.messaging.HttpMethod;

import java.util.List;
import java.util.Set;

public interface IHttpRequest extends IHttpMessage{

    void setMethod(HttpMethod method);
    HttpMethod getMethod();

    void setUrlTail(String tail);
    String getUrlTail();

}
