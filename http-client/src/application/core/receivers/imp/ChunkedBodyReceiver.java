package application.core.receivers.imp;

import application.core.receivers.api.IHttpBodyReceiver;
import application.core.receivers.exceptions.BodyReceiverException;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public class ChunkedBodyReceiver implements IHttpBodyReceiver {

    @Override
    public byte[] getBody(BufferedInputStream reader, Map<String, String> headers) throws BodyReceiverException {

        final int sleepTime = 50;
        boolean contentAvailable = true;
        ByteArrayOutputStream bodyStream = new ByteArrayOutputStream();
        try {

            do {
                // Introduce sleeping time so the server has time to chunk
                Thread.sleep(sleepTime);
                int nextChunkSize = readChunkSize(reader);
                if (nextChunkSize == 0) {
                    contentAvailable = false;
                } else {
                    byte[] chunkData = readChunkData(reader, nextChunkSize);
                    bodyStream.write(chunkData);
                    bodyStream.flush();
                }
            } while (contentAvailable);

        } catch (IOException e) {
            throw new BodyReceiverException("Could not collect bytes from chunk data");
        } catch (InterruptedException e) {
            throw new BodyReceiverException("Current thread failed to sleep");
        }
        return bodyStream.toByteArray();
    }

    private int readChunkSize(BufferedInputStream reader) throws BodyReceiverException {
        try {
            final String delimiter = "\r\n";
            StringBuilder builder = new StringBuilder();
            while (true) {
                char c = (char)reader.read();
                builder.append(c);
                if (builder.toString().endsWith(delimiter)) break;
            }
            String hexa = builder.toString();
            if (hexa.equals(delimiter)) return 0;
            return Integer.parseInt(hexa.substring(0, hexa.length()-2), 16);

        } catch (IOException e) {
            throw new BodyReceiverException("Could not read chunk size", e);
        } catch (NumberFormatException e) {
            throw new BodyReceiverException("The server responded with an incorrect chunk size", e);
        }
    }

    private byte[] readChunkData(BufferedInputStream reader, int count) throws BodyReceiverException {
        byte[] buffer = new byte[count];
        try {
            reader.read(buffer);
            return buffer;
        } catch (IOException e) {
            throw new BodyReceiverException("Could not read next chunk");
        }
    }
}
