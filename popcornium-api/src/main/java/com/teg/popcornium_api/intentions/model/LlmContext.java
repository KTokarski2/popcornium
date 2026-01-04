package com.teg.popcornium_api.intentions.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class LlmContext {

    private String retrievedContext;
    private Intention detectedBaseIntention;
    private Map<String, Object> attributes = new HashMap<>();

    public boolean hasRetrievedContext() {
        return retrievedContext != null && !retrievedContext.isBlank();
    }

    public void putAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }

    public static LlmContext empty() {
        return new LlmContext();
    }
}
