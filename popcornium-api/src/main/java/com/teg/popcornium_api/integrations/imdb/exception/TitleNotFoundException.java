package com.teg.popcornium_api.integrations.imdb.exception;

public class TitleNotFoundException extends RuntimeException {

    public TitleNotFoundException(String message) {
        super(message);
    }
}
