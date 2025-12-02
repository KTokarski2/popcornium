package com.teg.popcornium_api.integrations.filmweb.mapper;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ActorImportDto(
        @JsonProperty("full_name") String fullName,
        String role,
        @JsonProperty("photo_link") String photoLink
) {
}
