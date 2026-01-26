package com.teg.popcornium_api.common.controller;

import com.teg.popcornium_api.common.model.dto.MovieDetailsDto;
import com.teg.popcornium_api.common.model.dto.MovieDto;
import com.teg.popcornium_api.common.service.api.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public Page<MovieDto> getMovies(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Integer yearFrom,
            @RequestParam(required = false) Integer yearTo,
            @RequestParam(required = false) Double ratingFrom,
            @RequestParam(required = false) Double ratingTo,
            @RequestParam(defaultValue = "0") int page
    ) {
        return movieService.getMovies(
                query,
                yearFrom,
                yearTo,
                ratingFrom,
                ratingTo,
                page
        );
    }

    @GetMapping("/{movieId}")
    public MovieDetailsDto getMovieDetails(@PathVariable String movieId) {
        return movieService.getMovie(movieId);
    }
}
