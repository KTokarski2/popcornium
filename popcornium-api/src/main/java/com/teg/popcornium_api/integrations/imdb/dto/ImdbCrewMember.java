package com.teg.popcornium_api.integrations.imdb.dto;

import java.util.List;

public record ImdbCrewMember(
        String id,
        String displayName,
        List<String> alternativeNames,
        ImdbImage primaryImage,
        List<String> primaryProfessions
) {}
