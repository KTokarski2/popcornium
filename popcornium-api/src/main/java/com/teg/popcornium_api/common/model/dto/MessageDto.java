package com.teg.popcornium_api.common.model.dto;

import com.teg.popcornium_api.common.model.types.MessageSource;

public record MessageDto(
        String content,
        MessageSource source,
        String createdAt
) {}
