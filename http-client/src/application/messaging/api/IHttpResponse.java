package application.messaging.api;

import application.messaging.model.ResponseStatus;

import java.util.Set;

public interface IHttpResponse extends IHttpMessage {
    ResponseStatus getResponseStatus();
}
