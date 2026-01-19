package com.teg.popcornium_api.intentions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teg.popcornium_api.intentions.model.ExecutionStep;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExecutionPlanParser {

    private final ObjectMapper objectMapper;

    private static final String ERROR_FAILED_TO_PARSE = "Failed to parse execution plan from LLM: ";
    private static final String ERROR_NO_JSON_FOUND = "No JSON array found";

    public ExecutionPlanParser() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<ExecutionStep> parse(String rawResponse) {
        try {
            String json = extractJson(rawResponse);
            return objectMapper.readValue(
                    json,
                    new TypeReference<List<ExecutionStep>>() {}
            );
        } catch (Exception e) {
            throw new IllegalStateException(
                    ERROR_FAILED_TO_PARSE + rawResponse, e
            );
        }
    }

    private String extractJson(String text) {
        int start = text.indexOf("[");
        int end = text.lastIndexOf("]");
        if (start == -1 || end == -1 || end <= start) {
            throw new IllegalArgumentException(ERROR_NO_JSON_FOUND);
        }
        return text.substring(start, end + 1);
    }
}
