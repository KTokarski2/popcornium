package com.teg.popcornium_api.integrations.wikipedia.exception;

public class WikipediaApiException extends RuntimeException {

    public WikipediaApiException(String message) {
        super(message);
    }

    public WikipediaApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
