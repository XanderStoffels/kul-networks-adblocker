package application.core.client.receivers.imp;

import application.core.client.receivers.api.IHttpBodyReceiver;
import application.core.client.receivers.exceptions.BodyReceiverException;
import messaging.api.IHttpHeaders;

import java.io.BufferedInputStream;
import java.io.IOException;

public class ContentLengthBodyReceiver implements IHttpBodyReceiver {

    private int contentLength;

    public ContentLengthBodyReceiver(int contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public byte[] getBody(BufferedInputStream reader, IHttpHeaders headers) throws BodyReceiverException {
        byte[] content = new byte[this.contentLength];
        try {
            reader.read(content);
            return content;
        } catch (IOException e) {
            throw new BodyReceiverException("Could not receive body content", e);
        }
    }
}
