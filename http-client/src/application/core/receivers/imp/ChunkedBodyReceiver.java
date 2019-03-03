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
        Charset charset = findCharSet(headers);

        StringBuilder builder = new StringBuilder();
        boolean contentAvailable = true;

        do {
            int nextChunkSize = readChunkSize(reader);
            int nextCharCount = nextChunkSize / 8;

            if (nextCharCount == 0) {
                contentAvailable = false;
            } else {
                String chunkText = readChunkData(reader, nextCharCount);
                builder.append(chunkText);
            }
        } while (contentAvailable);

        return builder.toString();
    }

    private Charset findCharSet(Map<String, String> headers) throws BodyReceiverException {
        final String header = "Content-Type";
        if (!headers.containsKey(header))
            throw new BodyReceiverException("The response is missing the Content-Type header");

        String hValue = headers.get(header);
        List<String> entries = Arrays.stream(hValue.split(" ")).map(String::trim).collect(Collectors.toList());
        for (String entry : entries)
            if (entry.startsWith("charset="))
                try {
                    return stringToCharset(entry.split("=")[1]);
                } catch (IllegalArgumentException e) {
                    throw new BodyReceiverException("Error while finding charset for response-body");
                }

        throw new BodyReceiverException(String.format("No charset found in %s", header));
    }
    private Charset stringToCharset(String str) {
        Set<Charset> charsets = new HashSet<>(
                Arrays.asList(
                        StandardCharsets.ISO_8859_1,
                        StandardCharsets.US_ASCII,
                        StandardCharsets.UTF_8,
                        StandardCharsets.UTF_16,
                        StandardCharsets.UTF_16BE,
                        StandardCharsets.UTF_16LE
                ));

        for (Charset set : charsets)
            if (set.displayName().equals(str)) return set;

        throw new IllegalArgumentException(String.format("String could not be parsed to charset: %s", str));
    }

    private int readChunkSize(BufferedReader reader) throws BodyReceiverException {
        try {
            String chunkHexSize = reader.readLine();
            return Integer.parseInt(chunkHexSize, 16);
        } catch (IOException e) {
            throw new BodyReceiverException("Could not read chunk size", e);
        }
    }
    private String readChunkData(BufferedReader reader, int count) throws BodyReceiverException {
        char[] buffer = new char[count];
        try {
            reader.read(buffer);
            return new String(buffer);
        } catch (IOException e) {
            throw new BodyReceiverException("Could not read next chunk");
        }
    }
}
