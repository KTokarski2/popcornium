package com.teg.popcornium_api.prompts;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class PromptLoader {

    private static final String BASE_PATH = "prompts/";

    private static final String ERROR_FAILED_TO_LOAD_PROMPT = "Failed to load prompt: ";
    private static final String ERROR_PROMPT_NOT_FOUND = "Prompt not found: ";

    public String load(String relativePath) {
        try (InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream(BASE_PATH + relativePath)) {
            if (is == null) {
                throw new IllegalArgumentException(ERROR_PROMPT_NOT_FOUND + relativePath);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(ERROR_FAILED_TO_LOAD_PROMPT + relativePath, e);
        }
    }
}
