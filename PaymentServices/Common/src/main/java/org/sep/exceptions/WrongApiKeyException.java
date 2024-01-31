package org.sep.exceptions;

@SuppressWarnings("serial")
public class WrongApiKeyException extends RuntimeException {

    public WrongApiKeyException(String apiKey) {
        super("Wrong API key: " + apiKey);
    }
}