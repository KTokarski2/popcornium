package com.teg.popcornium_api.common.model.dto;

import com.teg.popcornium_api.common.model.types.UserRating;

import java.util.List;

public record MovieDetailsDto(
        String polishTitle,
        String originalTitle,
        Integer productionYear,
        UserRating userRating, //user
        Double rating, //filmweb
        String ratingCount, //filmweb
        byte[] poster,
        String directorName,
        List<String> descriptions,
        List<ActorDto> actors
) {}
