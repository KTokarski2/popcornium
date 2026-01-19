package com.teg.popcornium_api.seeder.filmweb.service;

import com.teg.popcornium_api.common.model.Movie;
import com.teg.popcornium_api.common.repository.MovieRepository;
import com.teg.popcornium_api.common.repository.WikipediaArticleRepository;
import com.teg.popcornium_api.integrations.wikipedia.exception.ArticleNotFoundException;
import com.teg.popcornium_api.integrations.wikipedia.service.api.WikipediaApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class WikipediaFetcherService {

    private final static String MSG_STARTING_ARTICLES_IMPORT = "Fetching wikipedia articles...";
    private final static String MSG_TO_IMPORT_COUNT = "{} movies in the database, starting fetch";
    private final static String MSG_PROGRESS = "Fetched {}/{} wikipedia articles";
    private final static String MSG_FETCH_COMPLETED = "Articles fetch completed successfully";

    private final MovieRepository movieRepository;
    private final WikipediaArticleRepository wikipediaArticleRepository;
    private final WikipediaApiService wikipediaApiService;

    public int fetchAndSaveWikipediaArticles() {
        log.info(MSG_STARTING_ARTICLES_IMPORT);
        int moviesCount = (int) movieRepository.count();
        int alreadyProcessed = 0;
        log.info(MSG_TO_IMPORT_COUNT, moviesCount);
        if (moviesCount > 0
                && wikipediaArticleRepository.count() == 0) {
            List<Movie> allMovies = movieRepository.findAll();
            for (Movie movie : allMovies) {
                try {
                    wikipediaApiService.fetchAndSaveArticleForMovie(movie.getId());
                    alreadyProcessed++;
                    log.info(MSG_PROGRESS, alreadyProcessed, moviesCount);
                    Thread.sleep(1000);
                } catch (ArticleNotFoundException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            log.info(MSG_FETCH_COMPLETED);
        }
        return alreadyProcessed;
    }
}
