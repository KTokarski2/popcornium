package com.teg.popcornium_api.common.model.dto;

import java.util.List;

public record ConversationDetail(
        String id,
        String title,
        List<MessageDto> messages
) {}
