package com.teg.popcornium_api.common.service;

import com.teg.popcornium_api.common.model.Movie;
import com.teg.popcornium_api.common.model.User;
import com.teg.popcornium_api.common.model.WatchlistEntry;
import com.teg.popcornium_api.common.model.dto.MovieDto;
import com.teg.popcornium_api.common.repository.MovieRepository;
import com.teg.popcornium_api.common.repository.WatchlistRepository;
import com.teg.popcornium_api.common.service.api.CurrentUserService;
import com.teg.popcornium_api.common.service.api.MovieService;
import com.teg.popcornium_api.common.service.api.WatchlistService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WatchlistServiceImpl implements WatchlistService {

    private final MovieRepository movieRepository;
    private final WatchlistRepository watchlistRepository;
    private final CurrentUserService currentUserService;
    private final MovieService movieService;


    @Transactional(readOnly = true)
    public List<MovieDto> getCurrentUserWatchlist() {
        User user = currentUserService.getCurrentUser();

        return user.getWatchlist().stream()
                .map(WatchlistEntry::getMovie)
                .map(movieService::mapToDto)
                .toList();
    }

    @Transactional
    public void addToWatchlist(String movieId) {

        User user = currentUserService.getCurrentUser();
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found"));

        watchlistRepository.findByUserAndMovie(user, movie)
                .orElseGet(() -> {
                    WatchlistEntry entry = new WatchlistEntry();
                    entry.setUser(user);
                    entry.setMovie(movie);
                    return watchlistRepository.save(entry);
                });
    }

    @Transactional
    public void removeFromWatchlist(String movieId) {

        User user = currentUserService.getCurrentUser();
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found"));

        watchlistRepository.findByUserAndMovie(user, movie)
                .ifPresent(watchlistRepository::delete);
    }
}
