package application.core.receivers.api;

import java.io.BufferedReader;
import java.util.Map;

public interface IHttpBodyReceiver {
    String getBody(BufferedReader reader);
}
