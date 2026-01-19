package com.teg.popcornium_api.common.repository;

import com.teg.popcornium_api.common.model.Movie;
import com.teg.popcornium_api.common.model.MovieRating;
import com.teg.popcornium_api.common.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRatingRepository extends JpaRepository<MovieRating, String> {

    Optional<MovieRating> findByUserAndMovie(User user, Movie movie);
}
