package com.teg.popcornium_api.common.model.dto;

public record ConversationSummary(
        String id,
        String title,
        String createdAt,
        String updatedAt
) {}
