package com.teg.popcornium_api.integrations.imdb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ImdbRating(
        @JsonProperty("aggregateRating")
        Double aggregateRating,
        @JsonProperty("voteCount")
        Integer voteCount
) {}
