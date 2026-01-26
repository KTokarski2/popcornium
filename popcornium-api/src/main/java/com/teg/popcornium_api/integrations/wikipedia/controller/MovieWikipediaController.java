package com.teg.popcornium_api.integrations.wikipedia.controller;

import com.teg.popcornium_api.common.model.WikipediaArticle;
import com.teg.popcornium_api.integrations.wikipedia.exception.ArticleNotFoundException;
import com.teg.popcornium_api.integrations.wikipedia.service.api.WikipediaApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/movies")
@RequiredArgsConstructor
public class MovieWikipediaController {

    private final WikipediaApiService wikipediaApiService;

    @PostMapping("/{movieId}/wikipedia")
    public ResponseEntity<WikipediaArticle> fetchWikipediaArticle(@PathVariable String movieId) throws ArticleNotFoundException {
        log.info("Received request to fetch Wikipedia article for movie ID: {}", movieId);

        WikipediaArticle article = wikipediaApiService.fetchAndSaveArticleForMovie(movieId);

        return ResponseEntity.ok(article);
    }
}
