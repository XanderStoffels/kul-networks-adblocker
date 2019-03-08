package application.core.receivers.api;

import application.core.receivers.exceptions.BodyReceiverException;

import java.io.BufferedInputStream;
import java.util.Map;

public interface IHttpBodyReceiver {
    byte[] getBody(BufferedInputStream reader, Map<String, String> headers) throws BodyReceiverException;
}
