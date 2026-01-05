package com.teg.popcornium_api.common.repository;

import com.teg.popcornium_api.common.model.Description;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DescriptionRepository extends JpaRepository<Description, String> {
    List<Description> findByMovieId(String movieId);
}
