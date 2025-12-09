package com.teg.popcornium_api.integrations.wikipedia.service;

import com.teg.popcornium_api.common.model.Language;
import com.teg.popcornium_api.common.model.Movie;
import com.teg.popcornium_api.common.model.WikipediaArticle;
import com.teg.popcornium_api.common.repository.MovieRepository;
import com.teg.popcornium_api.common.repository.WikipediaArticleRepository;
import com.teg.popcornium_api.integrations.wikipedia.client.api.WikipediaClient;
import com.teg.popcornium_api.integrations.wikipedia.dto.WikipediaArticleDto;
import com.teg.popcornium_api.integrations.wikipedia.exception.ArticleNotFoundException;
import com.teg.popcornium_api.integrations.wikipedia.service.api.WikipediaApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WikipediaApiServiceImpl implements WikipediaApiService {

    private static final String MSG_SEARCHING_ARTICLE_FOR_MOVIE = "Searching Wikipedia article for movie with ID: {}";
    private static final String MSG_MOVIE_NOT_FOUND = "Movie not found with ID: {}";
    private static final String MSG_USING_FIRST_SEARCH_RESULT = "Using first search result: {}";
    private static final String MSG_ARTICLE_ALREADY_EXISTS = "Wikipedia article already exists for movie ID: {} in language: {}";
    private static final String MSG_SUCCESSFULLY_SAVED_ARTICLE = "Successfully saved Wikipedia article for movie: {}";
    private static final String MSG_DISPLAYING_ARTICLE_ON_CONSOLE = "Displaying article on console for movie: {}";

    private static final String ERROR_NO_ARTICLE_FOUND = "No Wikipedia article found for movie with ID: ";
    private static final String ERROR_ARTICLE_CONTENT_NOT_AVAILABLE = "Article content not available for: ";
    private static final String ERROR_MOVIE_NOT_FOUND = "Movie not found with ID: ";

    private final WikipediaClient wikipediaClient;
    private final MovieRepository movieRepository;
    private final WikipediaArticleRepository wikipediaArticleRepository;

    @Override
    @Transactional
    public WikipediaArticle fetchAndSaveArticleForMovie(String movieId) throws ArticleNotFoundException {
        log.info(MSG_SEARCHING_ARTICLE_FOR_MOVIE, movieId);
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> {
                    log.error(MSG_MOVIE_NOT_FOUND, movieId);
                    return new ArticleNotFoundException(ERROR_MOVIE_NOT_FOUND + movieId);
                });
        var existingArticle = wikipediaArticleRepository
                .findByMovieIdAndLanguage(movieId, Language.PL);

        if (existingArticle.isPresent()) {
            log.info(MSG_ARTICLE_ALREADY_EXISTS, movieId, Language.PL);
            WikipediaArticle article = existingArticle.get();
            displayArticleOnConsole(article);
            return article;
        }
        String searchQuery = buildSearchQuery(movie);
        log.debug("Built search query: {}", searchQuery);

        List<String> searchResults = wikipediaClient.searchArticles(searchQuery);

        if (searchResults.isEmpty()) {
            log.warn("No results with year, trying without production year");
            searchQuery = movie.getPolishTitle() != null
                    ? movie.getPolishTitle()
                    : movie.getOriginalTitle();
            searchResults = wikipediaClient.searchArticles(searchQuery);
        }

        if (searchResults.isEmpty()) {
            throw new ArticleNotFoundException(ERROR_NO_ARTICLE_FOUND + movieId);
        }
        String bestMatch = findBestMatchingArticle(searchResults, movie);
        log.info(MSG_USING_FIRST_SEARCH_RESULT, bestMatch);

        WikipediaArticleDto articleDto = wikipediaClient.getArticleByTitle(bestMatch)
                .orElseThrow(() -> new ArticleNotFoundException(
                        ERROR_ARTICLE_CONTENT_NOT_AVAILABLE + bestMatch));
        if (!isMovieArticle(articleDto, movie)) {
            log.warn("Found article doesn't seem to be about the movie. Title: {}", articleDto.title());
        }
        WikipediaArticle wikipediaArticle = createWikipediaArticle(articleDto, movie);
        WikipediaArticle savedArticle = wikipediaArticleRepository.save(wikipediaArticle);
        log.info(MSG_SUCCESSFULLY_SAVED_ARTICLE, movie.getPolishTitle());
        displayArticleOnConsole(savedArticle);
        return savedArticle;
    }

    private WikipediaArticle createWikipediaArticle(WikipediaArticleDto dto, Movie movie) {
        LocalDateTime now = LocalDateTime.now();

        WikipediaArticle article = new WikipediaArticle();
        article.setCreated(now);
        article.setModified(now);
        article.setText(dto.content());
        article.setLanguage(Language.PL);
        article.setMovie(movie);

        return article;
    }

    private String buildSearchQuery(Movie movie) {
        String title = movie.getPolishTitle() != null
                ? movie.getPolishTitle()
                : movie.getOriginalTitle();
        if (movie.getProductionYear() != null) {
            return title + " " + movie.getProductionYear() + " film";
        }
        return title + " film";
    }

    private String findBestMatchingArticle(List<String> results, Movie movie) {
        Integer productionYear = movie.getProductionYear();

        for (String result : results) {
            String lowerResult = result.toLowerCase();
            if (productionYear != null &&
                    lowerResult.contains(String.valueOf(productionYear)) &&
                    lowerResult.contains("film")) {
                return result;
            }
        }
        for (String result : results) {
            if (result.toLowerCase().contains("(film")) {
                return result;
            }
        }
        return results.getFirst();
    }

    private boolean isMovieArticle(WikipediaArticleDto article, Movie movie) {
        String content = article.content().toLowerCase();
        String title = article.title().toLowerCase();
        boolean hasMovieKeywords = content.contains("film") ||
                content.contains("reżyseria") ||
                content.contains("produkcja") ||
                title.contains("(film");
        boolean hasProductionYear = movie.getProductionYear() != null &&
                content.contains(String.valueOf(movie.getProductionYear()));

        return hasMovieKeywords || hasProductionYear;
    }

    private void displayArticleOnConsole(WikipediaArticle article) {
        log.debug(MSG_DISPLAYING_ARTICLE_ON_CONSOLE, article.getMovie().getPolishTitle());

        System.out.println("=".repeat(80));
        System.out.println("WIKIPEDIA ARTICLE");
        System.out.println("=".repeat(80));
        System.out.println("Movie: " + article.getMovie().getPolishTitle()
                + " (" + article.getMovie().getOriginalTitle() + ")");
        System.out.println("Language: " + article.getLanguage().getFullName());
        System.out.println("Created: " + article.getCreated());
        System.out.println("=".repeat(80));
        System.out.println("\nCONTENT:");
        System.out.println(article.getText());
        System.out.println("\n" + "=".repeat(80));
    }
}
