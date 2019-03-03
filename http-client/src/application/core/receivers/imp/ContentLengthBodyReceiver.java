package application.core.receivers.imp;

import application.core.receivers.api.IHttpBodyReceiver;
import application.core.receivers.exceptions.BodyReceiverException;

import java.io.BufferedReader;
import java.io.IOException;

public class ContentLengthBodyReceiver implements IHttpBodyReceiver {

    private int contentLength;

    public ContentLengthBodyReceiver(int contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public String getBody(BufferedReader reader) throws BodyReceiverException {
        char[] content = new char[this.contentLength];
        try {
            reader.read(content);
            return new String(content);
        } catch (IOException e) {
            throw new BodyReceiverException("Could not receive body content", e);
        }
    }
}
