package com.teg.popcornium_api.intentions.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ExecutionStep(
        Intention intention,
        @JsonProperty("query") String stepQuery,
        List<String> dependsOn,
        String outputKey,
        Boolean allowRag
) {}
