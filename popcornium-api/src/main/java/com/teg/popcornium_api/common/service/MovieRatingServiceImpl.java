package com.teg.popcornium_api.common.service;

import com.teg.popcornium_api.common.model.Movie;
import com.teg.popcornium_api.common.model.MovieRating;
import com.teg.popcornium_api.common.model.User;
import com.teg.popcornium_api.common.model.types.UserRating;
import com.teg.popcornium_api.common.repository.MovieRatingRepository;
import com.teg.popcornium_api.common.repository.MovieRepository;
import com.teg.popcornium_api.common.service.api.CurrentUserService;
import com.teg.popcornium_api.common.service.api.MovieRatingService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MovieRatingServiceImpl implements MovieRatingService {

    private final MovieRepository movieRepository;
    private final MovieRatingRepository movieRatingRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public void likeMovie(String movieId) {
        rate(movieId, UserRating.LIKE);
    }

    @Transactional
    public void dislikeMovie(String movieId) {
        rate(movieId, UserRating.DISLIKE);
    }

    private void rate(String movieId, UserRating rating) {

        User user = currentUserService.getCurrentUser();

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found"));

        MovieRating movieRating = movieRatingRepository
                .findByUserAndMovie(user, movie)
                .orElseGet(() -> {
                    MovieRating r = new MovieRating();
                    r.setUser(user);
                    r.setMovie(movie);
                    return r;
                });

        movieRating.setRating(rating);
        movieRatingRepository.save(movieRating);
    }
}