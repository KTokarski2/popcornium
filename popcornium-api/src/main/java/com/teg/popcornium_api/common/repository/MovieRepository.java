package com.teg.popcornium_api.common.repository;

import com.teg.popcornium_api.common.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, String> {
    Optional<Movie> findByOriginalTitleAndProductionYear(String originalTitle, Integer productionYear);

    @Query("SELECT m FROM Movie m " +
            "LEFT JOIN FETCH m.descriptions d " +
            "LEFT JOIN FETCH m.movieActors ma " +
            "LEFT JOIN FETCH ma.actor a " +
            "LEFT JOIN FETCH m.movieCategories mc " +
            "LEFT JOIN FETCH mc.category c " +
            "LEFT JOIN FETCH m.director dir")
    List<Movie> findAllWithDetails();
}
