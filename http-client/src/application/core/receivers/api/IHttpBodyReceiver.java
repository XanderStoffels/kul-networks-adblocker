package application.core.receivers.api;

import application.core.receivers.exceptions.BodyReceiverException;

import java.io.BufferedReader;
import java.util.Map;

public interface IHttpBodyReceiver {
    String getBody(BufferedReader reader, Map<String, String> headers) throws BodyReceiverException;
}
