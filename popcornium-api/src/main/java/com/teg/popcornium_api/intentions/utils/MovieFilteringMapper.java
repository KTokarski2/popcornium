package com.teg.popcornium_api.intentions.utils;

import com.teg.popcornium_api.common.model.Movie;
import com.teg.popcornium_api.intentions.model.MovieFilteringResultDto;

import java.util.stream.Collectors;

public class MovieFilteringMapper {

    public static MovieFilteringResultDto map(Movie m) {
        return new MovieFilteringResultDto(
                m.getId(),
                m.getPolishTitle(),
                m.getOriginalTitle(),
                m.getProductionYear(),
                m.getRating(),
                RatingCountParser.parse(m.getRatingCount()),
                m.getDirector() != null ? m.getDirector().getName() : null,
                m.getMovieActors().stream()
                        .map(ma -> ma.getActor().getName())
                        .collect(Collectors.toSet()),
                m.getMovieCategories().stream()
                        .map(mc -> mc.getCategory().getName())
                        .collect(Collectors.toSet()),
                !m.getWikipediaArticles().isEmpty()
        );
    }
}
