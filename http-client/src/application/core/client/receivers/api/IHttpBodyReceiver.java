package application.core.client.receivers.api;

import application.core.client.receivers.exceptions.BodyReceiverException;
import messaging.api.IHttpHeaders;

import java.io.BufferedInputStream;

public interface IHttpBodyReceiver {
    byte[] getBody(BufferedInputStream reader, IHttpHeaders headers) throws BodyReceiverException;
}
