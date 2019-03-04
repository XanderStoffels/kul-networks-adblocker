package application.core.receivers.imp;

import application.core.receivers.api.IHttpBodyReceiver;
import application.core.receivers.exceptions.BodyReceiverException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

public class ChunkedBodyReceiver implements IHttpBodyReceiver {


    @Override
    public String getBody(BufferedReader reader, Map<String, String> headers) throws BodyReceiverException {

        // Find out the body charset
        // Todo: find out how many bytes each charset char requires
        StringBuilder builder = new StringBuilder();
        boolean contentAvailable = true;

        do {
            int nextChunkSize = readChunkSize(reader);

            if (nextChunkSize == 0) {
                contentAvailable = false;
            } else {
                String chunkText = readChunkData(reader, nextChunkSize);
                builder.append(chunkText);
            }
        } while (contentAvailable);

        return builder.toString();
    }

    private int readChunkSize(BufferedReader reader) throws BodyReceiverException {
        try {
            String chunkHexSize = reader.readLine();
            return Integer.parseInt(chunkHexSize, 16);
        } catch (IOException e) {
            throw new BodyReceiverException("Could not read chunk size", e);
        } catch (NumberFormatException e) {
            throw new BodyReceiverException("The server responded with an incorrect chunk size", e);
        }
    }
    private String readChunkData(BufferedReader reader, int count) throws BodyReceiverException {
        char[] buffer = new char[count];
        try {
            reader.read(buffer);
            reader.readLine();
            return new String(buffer);
        } catch (IOException e) {
            throw new BodyReceiverException("Could not read next chunk");
        }
    }
}
