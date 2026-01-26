package com.teg.popcornium_api.common.model.dto;

import lombok.Builder;

@Builder
public record ChatQuery(
        String query,
        String conversationId,
        String ragType
) {}
