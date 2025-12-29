package com.teg.popcornium_api.seeder.filmweb.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ActorImportDto(
        @JsonProperty("full_name") String fullName,
        String role,
        @JsonProperty("photo_link") String photoLink
) {
}
