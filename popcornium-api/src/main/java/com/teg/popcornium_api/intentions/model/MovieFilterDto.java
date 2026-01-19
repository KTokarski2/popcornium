package com.teg.popcornium_api.intentions.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MovieFilterDto(
        String title,
        String genre,
        String director,
        String actor,
        Integer yearFrom,
        Integer yearTo,
        Double rating,
        Integer ratingCount
) {}
