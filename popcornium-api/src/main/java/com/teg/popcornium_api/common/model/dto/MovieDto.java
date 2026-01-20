package com.teg.popcornium_api.common.model.dto;

public record MovieDto(String id, String polishTitle, String originalTitle, int releaseYear, String rating, byte[] image) {
}
