package com.teg.popcornium_api.intentions.utils;

import com.azure.json.implementation.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teg.popcornium_api.intentions.model.MovieFilteringResultDto;

import java.util.List;
import java.util.Map;

public class FilteringContextBuilder {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static String build(List<MovieFilteringResultDto> results)
            throws JsonProcessingException, com.fasterxml.jackson.core.JsonProcessingException {
        List<MovieFilteringResultDto> limited =
                results.size() > 30 ? results.subList(0, 30) : results;
        Map<String, Object> payload = Map.of(
                "resultCount", results.size(),
                "movies", limited
        );
        return MAPPER.writerWithDefaultPrettyPrinter()
                .writeValueAsString(payload);
    }
}
