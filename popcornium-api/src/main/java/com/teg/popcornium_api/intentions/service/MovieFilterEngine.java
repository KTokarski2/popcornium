package com.teg.popcornium_api.intentions.service;

import com.teg.popcornium_api.common.model.Movie;
import com.teg.popcornium_api.intentions.model.MovieFilterDto;
import com.teg.popcornium_api.intentions.utils.RatingCountParser;

import java.util.function.Predicate;

public class MovieFilterEngine {

    public static Predicate<Movie> build(MovieFilterDto f) {
        return movie ->
                matchTitle(movie, f)
                        && matchYear(movie, f)
                        && matchRating(movie, f)
                        && matchDirector(movie, f)
                        && matchActor(movie, f)
                        && matchGenre(movie, f);
    }

    private static boolean matchTitle(Movie m, MovieFilterDto f) {
        if (f.title() == null) return true;
        String t = f.title().toLowerCase();
        return (m.getPolishTitle() != null && m.getPolishTitle().toLowerCase().contains(t))
                || (m.getOriginalTitle() != null && m.getOriginalTitle().toLowerCase().contains(t));
    }

    private static boolean matchYear(Movie m, MovieFilterDto f) {
        if (f.yearFrom() != null && m.getProductionYear() < f.yearFrom()) return false;
        if (f.yearTo() != null && m.getProductionYear() > f.yearTo()) return false;
        return true;
    }

    private static boolean matchRating(Movie m, MovieFilterDto f) {
        if (f.rating() != null && (m.getRating() == null || m.getRating() < f.rating()))
            return false;

        if (f.ratingCount() != null) {
            Integer parsed = RatingCountParser.parse(m.getRatingCount());
            return parsed != null && parsed >= f.ratingCount();
        }

        return true;
    }

    private static boolean matchDirector(Movie m, MovieFilterDto f) {
        if (f.director() == null) return true;
        return m.getDirector() != null
                && m.getDirector().getName().toLowerCase().contains(f.director().toLowerCase());
    }

    private static boolean matchActor(Movie m, MovieFilterDto f) {
        if (f.actor() == null) return true;
        return m.getMovieActors().stream()
                .anyMatch(ma ->
                        ma.getActor().getName().toLowerCase().contains(f.actor().toLowerCase())
                );
    }

    private static boolean matchGenre(Movie m, MovieFilterDto f) {
        if (f.genre() == null) return true;
        return m.getMovieCategories().stream()
                .anyMatch(mc ->
                        mc.getCategory().getName().toLowerCase().contains(f.genre().toLowerCase())
                );
    }
}
