package com.teg.popcornium_api.intentions.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class LlmContext {

    private Intention detectedBaseIntention;
    private Map<String, Object> attributes = new HashMap<>();
    private String finalContext;

    public boolean hasFinalContext() {
        return finalContext != null && !finalContext.isBlank();
    }

    public void putAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }

    public boolean hasAttribute(String key) {
        return attributes.containsKey(key);
    }

    public boolean hasAny() {
        return !attributes.isEmpty();
    }

    public static LlmContext empty() {
        return new LlmContext();
    }

    public void buildFinalContext() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n\nYou have the following structured intermediate results:\n\n");
        attributes.forEach((key, value) -> {
            sb.append(key).append(":\n");
            sb.append(value).append("\n\n");
        });
        sb.append("Use this information to answer the user's original question.");
        this.setFinalContext(sb.toString());
    }
}
