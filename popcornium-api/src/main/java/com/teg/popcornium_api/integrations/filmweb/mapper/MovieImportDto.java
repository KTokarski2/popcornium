package com.teg.popcornium_api.integrations.filmweb.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public record MovieImportDto(
        @JsonProperty("polish_title") String polishTitle,
        @JsonProperty("english_title") String englishTitle,
        @JsonProperty("production_year") String productionYear,
        @JsonProperty("rating") String rating,
        @JsonProperty("rating_count") String ratingCount,
        @JsonProperty("poster_url") String posterUrl,
        List<String> descriptions,
        Map<String, ActorImportDto> actors
) {
}
