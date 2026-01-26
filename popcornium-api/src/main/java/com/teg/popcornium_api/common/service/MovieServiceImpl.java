package com.teg.popcornium_api.common.service;

import com.teg.popcornium_api.common.model.*;
import com.teg.popcornium_api.common.model.dto.ActorDto;
import com.teg.popcornium_api.common.model.dto.MovieDetailsDto;
import com.teg.popcornium_api.common.model.dto.MovieDto;
import com.teg.popcornium_api.common.model.types.UserRating;
import com.teg.popcornium_api.common.repository.MovieRepository;
import com.teg.popcornium_api.common.service.api.CurrentUserService;
import com.teg.popcornium_api.common.service.api.MovieService;
import com.teg.popcornium_api.common.util.MovieSpecification;
import com.teg.popcornium_api.integrations.minio.service.MinioService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {

    private static final int PAGE_SIZE = 50;

    private final MovieRepository movieRepository;
    private final MinioService minioService;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public Page<MovieDto> getMovies(
            String query,
            Integer yearFrom,
            Integer yearTo,
            Double ratingFrom,
            Double ratingTo,
            int page
    ) {
        Pageable pageable = PageRequest.of(
                page,
                PAGE_SIZE,
                Sort.by("productionYear").descending()
        );

        Specification<Movie> spec = MovieSpecification.filter(
                query,
                yearFrom,
                yearTo,
                ratingFrom,
                ratingTo
        );

        return movieRepository.findAll(spec, pageable)
                .map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    @Override
    public MovieDetailsDto getMovie(String id) {
        Movie movie = movieRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Movie not found: " + id));
        return mapMovieToDto(movie);
    }

    @Override
    public MovieDto mapToDto(Movie movie) {
        byte[] poster = null;

        if (movie.getPosterMinioId() != null) {
            poster = minioService.download(movie.getPosterMinioId());
        }

        return new MovieDto(
                movie.getId(),
                movie.getPolishTitle(),
                movie.getOriginalTitle(),
                movie.getProductionYear(),
                movie.getRating() != null ? movie.getRating().toString() : null,
                poster
        );
    }

    private MovieDetailsDto mapMovieToDto(Movie movie) {

        byte[] poster = movie.getPosterMinioId() != null
                ? minioService.download(movie.getPosterMinioId()) : null;

        String directorName = movie.getDirector() != null
                ? movie.getDirector().getName()
                : null;

        List<String> descriptions = movie.getDescriptions()
                .stream()
                .map(Description::getText)
                .toList();

        List<ActorDto> actors = movie.getMovieActors()
                .stream()
                .map(this::mapActor)
                .toList();

        UserRating rating = resolveUserRating(movie);

        return new MovieDetailsDto(
                movie.getPolishTitle(),
                movie.getOriginalTitle(),
                movie.getProductionYear(),
                rating,
                movie.getRating(),
                movie.getRatingCount(),
                poster,
                directorName,
                descriptions,
                actors
        );
    }

    private UserRating resolveUserRating(Movie movie) {
        return currentUserService.getCurrentUserOptional()
                .flatMap(user ->
                        movie.getRatings()
                                .stream()
                                .filter(r -> r.getUser().getId().equals(user.getId()))
                                .findFirst()
                )
                .map(MovieRating::getRating)
                .orElse(null);
    }

    private ActorDto mapActor(MovieActor movieActor) {
        Actor actor = movieActor.getActor();

        return new ActorDto(
                actor.getName(),
                actor.getPhotoUrl()
        );
    }
}
