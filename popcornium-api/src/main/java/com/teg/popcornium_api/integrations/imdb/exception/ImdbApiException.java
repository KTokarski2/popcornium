package com.teg.popcornium_api.integrations.imdb.exception;

public class ImdbApiException extends RuntimeException {

    public ImdbApiException(String message) {
        super(message);
    }

    public ImdbApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
