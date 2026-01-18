package com.teg.popcornium_api.common.service.api;

import com.teg.popcornium_api.common.model.dto.MovieDto;

import java.util.List;

public interface WatchlistService {
    void addToWatchlist(String movieId);
    void removeFromWatchlist(String movieId);
    List<MovieDto> getCurrentUserWatchlist();
}
