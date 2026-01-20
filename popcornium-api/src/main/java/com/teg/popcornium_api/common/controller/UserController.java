package com.teg.popcornium_api.common.controller;

import com.teg.popcornium_api.common.model.dto.CompletionDto;
import com.teg.popcornium_api.common.model.dto.MovieDto;
import com.teg.popcornium_api.common.service.api.CompletionService;
import com.teg.popcornium_api.common.service.api.MovieRatingService;
import com.teg.popcornium_api.common.service.api.WatchlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final CompletionService completionService;
    private final WatchlistService watchlistService;
    private final MovieRatingService movieRatingService;

    @GetMapping("/completions")
    public List<CompletionDto> getCompletionsForUser() {
        return completionService.getCompletionsForUser();
    }

    @GetMapping("/watchlist")
    public List<MovieDto> getWatchlist() {
        return watchlistService.getCurrentUserWatchlist();
    }

    @PostMapping("/movies/{movieId}/like")
    public ResponseEntity<Void> like(@PathVariable String movieId) {
        movieRatingService.likeMovie(movieId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/movies/{movieId}/dislike")
    public ResponseEntity<Void> dislike(@PathVariable String movieId) {
        movieRatingService.dislikeMovie(movieId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/movies/{movieId}/watchlist")
    public ResponseEntity<Void> addToWatchlist(@PathVariable String movieId) {
        watchlistService.addToWatchlist(movieId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/movies/{movieId}/watchlist")
    public ResponseEntity<Void> removeFromWatchlist(@PathVariable String movieId) {
        watchlistService.removeFromWatchlist(movieId);
        return ResponseEntity.noContent().build();
    }
}
