package com.teg.popcornium_api.common.service.api;

public interface MovieRatingService {
    void likeMovie(String movieId);

    void dislikeMovie(String movieId);
}
