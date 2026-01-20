package com.teg.popcornium_api.common.repository;

import com.teg.popcornium_api.common.model.Movie;
import com.teg.popcornium_api.common.model.User;
import com.teg.popcornium_api.common.model.WatchlistEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WatchlistRepository extends JpaRepository<WatchlistEntry, String> {
    Optional<WatchlistEntry> findByUserAndMovie(User user, Movie movie);
}
