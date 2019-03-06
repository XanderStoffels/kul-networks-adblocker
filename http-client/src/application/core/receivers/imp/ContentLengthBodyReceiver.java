package application.core.receivers.imp;

import application.core.receivers.api.IHttpBodyReceiver;
import application.core.receivers.exceptions.BodyReceiverException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class ContentLengthBodyReceiver implements IHttpBodyReceiver {

    private int contentLength;

    public ContentLengthBodyReceiver(int contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public byte[] getBody(BufferedReader reader, Map<String, String> headers) throws BodyReceiverException {
        char[] content = new char[this.contentLength];
        try {
            reader.read(content);
            return new String(content).getBytes();
        } catch (IOException e) {
            throw new BodyReceiverException("Could not receive body content", e);
        }
    }
}
