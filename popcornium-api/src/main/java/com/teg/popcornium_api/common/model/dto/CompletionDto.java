package com.teg.popcornium_api.common.model.dto;

public record CompletionDto(
        String id,
        String model,
        String prompt,
        String response,
        Integer promptTokens,
        Integer completionTokens,
        Integer totalTokens,
        Double temperature,
        Integer maxTokens,
        String finishReason,
        String metadata
) {}
