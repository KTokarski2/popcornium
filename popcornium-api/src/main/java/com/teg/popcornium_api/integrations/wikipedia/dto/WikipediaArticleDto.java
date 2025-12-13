package com.teg.popcornium_api.integrations.wikipedia.dto;

import lombok.Builder;

@Builder
public record WikipediaArticleDto(
       String title,
       String content,
       String url
) {}
