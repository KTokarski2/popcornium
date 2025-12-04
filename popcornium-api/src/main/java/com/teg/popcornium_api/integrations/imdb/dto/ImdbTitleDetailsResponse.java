package com.teg.popcornium_api.integrations.imdb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ImdbTitleDetailsResponse(
        String id,
        String type,
        String primaryTitle,
        @JsonProperty("primaryImage")
        ImdbImage primaryImage,
        @JsonProperty("startYear")
        Integer year,
        Integer runtimeSeconds,
        List<String> genres,
        ImdbRating rating,
        String plot,
        List<ImdbCrewMember> directors,
        List<ImdbCrewMember> writers,
        List<ImdbCrewMember> stars,
        List<ImdbOriginCountry> originCountries
) {}
