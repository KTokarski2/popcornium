package com.teg.popcornium_api.prompts;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PromptRenderer {

    public String render(String template, Map<String, String> variables) {
        String result = template;
        for (var entry : variables.entrySet()) {
            result = result.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return result;
    }
}
