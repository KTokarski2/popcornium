package com.teg.popcornium_api.integrations.imdb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ImdbTitle(
    String id,
    String type,
    @JsonProperty("primaryTitle")
    String title,
    @JsonProperty("originalTitle")
    String originalTitle,
    @JsonProperty("primaryImage")
    ImdbImage primaryImage,
    @JsonProperty("startYear")
    Integer year,
    ImdbRating rating
) {}
