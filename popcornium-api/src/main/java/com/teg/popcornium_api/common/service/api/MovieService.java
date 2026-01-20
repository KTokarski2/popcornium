package com.teg.popcornium_api.common.service.api;

import com.teg.popcornium_api.common.model.Movie;
import com.teg.popcornium_api.common.model.dto.MovieDetailsDto;
import com.teg.popcornium_api.common.model.dto.MovieDto;
import org.springframework.data.domain.Page;

public interface MovieService {

    Page<MovieDto> getMovies(
            String query,
            Integer yearFrom,
            Integer yearTo,
            Double ratingFrom,
            Double ratingTo,
            int page
    );

    MovieDetailsDto getMovie(String id);

    MovieDto mapToDto(Movie movie);
}
