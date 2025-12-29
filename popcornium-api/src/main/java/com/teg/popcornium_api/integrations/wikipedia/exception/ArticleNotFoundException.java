package com.teg.popcornium_api.integrations.wikipedia.exception;

public class ArticleNotFoundException extends Exception {
    public ArticleNotFoundException(String message) {
        super(message);
    }
    public ArticleNotFoundException(String message, Throwable cause) {}
}
