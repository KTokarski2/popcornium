package com.teg.popcornium_api.intentions.model;

import java.util.Set;

public record MovieFilteringResultDto(
        String id,
        String polishTitle,
        String originalTitle,
        Integer productionYear,
        Double rating,
        Integer ratingCount,
        String director,
        Set<String> actors,
        Set<String> genres,
        boolean hasWikipediaArticle
) {}


