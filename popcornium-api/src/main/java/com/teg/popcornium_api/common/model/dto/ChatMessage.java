package com.teg.popcornium_api.common.model.dto;

import lombok.Builder;

@Builder
public record ChatMessage(
        String role,
        String content
) {}
