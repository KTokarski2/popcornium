package com.teg.popcornium_api.common.repository;

import com.teg.popcornium_api.common.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, String> {
    Optional<Movie> findByOriginalTitleAndProductionYear(String originalTitle, Integer productionYear);

    @Query("SELECT DISTINCT m FROM Movie m " +
            "LEFT JOIN FETCH m.movieCategories mc " +
            "LEFT JOIN FETCH mc.category " +
            "LEFT JOIN FETCH m.descriptions " +
            "WHERE m.id = :id")
    Optional<Movie> findByIdWithCategories(@Param("id") String id);
}
