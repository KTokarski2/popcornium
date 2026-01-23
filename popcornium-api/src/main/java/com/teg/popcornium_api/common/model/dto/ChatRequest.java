package com.teg.popcornium_api.common.model.dto;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record ChatRequest(
   String systemPrompt,
   String userMessage,
   String context,
   Integer maxTokens,
   Double temperature,
   Map<String, String> metadata
) {}
