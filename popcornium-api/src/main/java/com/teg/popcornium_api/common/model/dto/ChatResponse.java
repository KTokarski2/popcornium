package com.teg.popcornium_api.common.model.dto;

import lombok.Builder;

@Builder
public record ChatResponse(
        String content,
        String model,
        Integer promptTokens,
        Integer completionTokens,
        Integer totalTokens,
        String finishReason
) {}
