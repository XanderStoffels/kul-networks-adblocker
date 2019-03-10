package messaging.api;

import messaging.model.ResponseStatus;


public interface IHttpResponse extends IHttpMessage {
    ResponseStatus getResponseStatus();
    byte[] serialize();
}
