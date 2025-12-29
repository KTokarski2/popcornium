package com.teg.popcornium_api.seeder.filmweb.service;

import com.teg.popcornium_api.common.model.*;
import com.teg.popcornium_api.common.repository.CategoryRepository;
import com.teg.popcornium_api.common.repository.DirectorRepository;
import com.teg.popcornium_api.common.repository.MovieRepository;
import com.teg.popcornium_api.integrations.imdb.dto.ImdbTitle;
import com.teg.popcornium_api.integrations.imdb.dto.ImdbTitleDetailsResponse;
import com.teg.popcornium_api.integrations.imdb.service.api.ImdbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImdbFetcherService {

    private static final String MSG_STARTING_MISSING_DATA_FETCH = "Starting to fetch missing data";
    private static final String MSG_DATA_TO_PROCESS = "{} movies in the database, starting process";
    private static final String MSG_SETTING_EXISTING_DIRECTOR = "Setting already existing director: {} for movie: {}";
    private static final String MSG_CREATING_NEW_DIRECTOR = "Creating new director: {} for movie: {}";
    private static final String MSG_ASSIGNING_CATEGORY = "Assigning category: {} for movie: {}";
    private static final String MSG_CREATING_NEW_CATEGORY = "Creating new category: {} for movie: {}";
    private static final String MSG_ADDING_IMDB_PLOT = "Adding new imdb description for movie: {}";
    private static final String MSG_SLEEPING = "Sleeping for {} ms";
    private static final String MSG_SLEEP_INTERRUPT = "Sleep interrupted";
    private static final String MSG_RETRYING = "Retrying after {} ms";
    private static final String MSG_COMPLETED_PROCESSING = "Completed processing. Success: {}, Failed: {}";
    private static final String MSG_MOVIE_NOT_FOUND = "Movie not found: ";
    private static final String MSG_RATE_LIMIT_HIT = "Rate limit hit for movie: {}. Waiting {} seconds before retry {}/{}";

    private static final String ERROR_FAILED_AFTER_RETRIES = "Failed to process movie after retries, moving to next. Success: {}, Failed: {}";
    private static final String ERROR_FETCH_FAILED = "Failed to fetch data for movie: {}, attempt {}/{}, error: {}";
    private static final String ERROR_FAILED_TO_PROCESS_MOVIE = "Failed to process movie: {} after {} retries";

    private final MovieRepository movieRepository;
    private final ImdbService imdbService;
    private final DirectorRepository directorRepository;
    private final CategoryRepository categoryRepository;

    private static final long INITIAL_DELAY_MS = 3000;
    private static final long RATE_LIMIT_DELAY_MS = 20000;
    private static final int MAX_RETRIES = 3;
    private static final String TOO_MANY_REQUESTS_ERROR_CODE = "429";

    public void alignMoviesData() {
        log.info(MSG_STARTING_MISSING_DATA_FETCH);
        long moviesCount = movieRepository.count();
        if (moviesCount > 0) {
            log.info(MSG_DATA_TO_PROCESS, moviesCount);
            List<String> movieIds = movieRepository.findAll().stream()
                    .map(Movie::getId)
                    .toList();

            int successCount = 0;
            int failureCount = 0;

            for (String id : movieIds) {
                boolean success = fetchAndSaveDataTransactional(id);
                if (success) {
                    successCount++;
                    sleep(INITIAL_DELAY_MS);
                } else {
                    failureCount++;
                    log.warn(ERROR_FAILED_AFTER_RETRIES,
                            successCount, failureCount);
                    sleep(INITIAL_DELAY_MS);
                }
            }

            log.info(MSG_COMPLETED_PROCESSING, successCount, failureCount);
        }
    }

    @Transactional
    public boolean fetchAndSaveDataTransactional(String movieId) {
        Movie movie = movieRepository.findByIdWithCategories(movieId)
                .orElseThrow(() -> new RuntimeException(MSG_MOVIE_NOT_FOUND + movieId));

        boolean success = fetchAndSaveDataWithRetry(movie);
        if (success) {
            movieRepository.save(movie);
        }
        return success;
    }

    private boolean fetchAndSaveDataWithRetry(Movie movie) {
        int retryCount = 0;
        while (retryCount < MAX_RETRIES) {
            try {
                fetchAndSaveData(movie);
                return true;
            } catch (Exception e) {
                retryCount++;
                if (e.getMessage() != null && e.getMessage().contains(TOO_MANY_REQUESTS_ERROR_CODE)) {
                    log.warn(MSG_RATE_LIMIT_HIT,
                            movie.getOriginalTitle(), RATE_LIMIT_DELAY_MS / 1000, retryCount, MAX_RETRIES);
                    sleep(RATE_LIMIT_DELAY_MS);
                } else {
                    log.error(ERROR_FETCH_FAILED,
                            movie.getOriginalTitle(), retryCount, MAX_RETRIES, e.getMessage());

                    if (retryCount < MAX_RETRIES) {
                        long backoffDelay = (long) (INITIAL_DELAY_MS * Math.pow(2, retryCount));
                        log.info(MSG_RETRYING, backoffDelay);
                        sleep(backoffDelay);
                    }
                }
            }
        }
        log.error(ERROR_FAILED_TO_PROCESS_MOVIE, movie.getOriginalTitle(), MAX_RETRIES);
        return false;
    }

    private void fetchAndSaveData(Movie movie) {
        List<ImdbTitle> found = imdbService.searchTitles(movie.getOriginalTitle(), 1);
        if (!found.isEmpty()) {
            ImdbTitleDetailsResponse response = imdbService.getTitleDetails(found.getFirst().id());
            handleDirector(movie, response);
            handleGenres(movie, response);
            addImdbDescription(movie, response);
        }
    }

    private void handleDirector(Movie movie, ImdbTitleDetailsResponse response) {
        String name = response.directors().getFirst().displayName();
        Director director = directorRepository.findByName(name)
                .orElseGet(() -> {
                    log.info(MSG_CREATING_NEW_DIRECTOR, name, movie.getOriginalTitle());
                    Director newDirector = new Director();
                    newDirector.setName(name);
                    return directorRepository.save(newDirector);
                });

        log.info(MSG_SETTING_EXISTING_DIRECTOR, name, movie.getOriginalTitle());
        movie.setDirector(director);
    }

    private void handleGenres(Movie movie, ImdbTitleDetailsResponse response) {
        response.genres().forEach(genre -> {
            Category category = categoryRepository.findByName(genre)
                    .orElseGet(() -> {
                        log.info(MSG_CREATING_NEW_CATEGORY, genre, movie.getOriginalTitle());
                        Category c = new Category();
                        c.setName(genre);
                        return categoryRepository.save(c);
                    });

            log.info(MSG_ASSIGNING_CATEGORY, category.getName(), movie.getOriginalTitle());
            movie.addCategory(category);
        });
    }

    private void addImdbDescription(Movie movie, ImdbTitleDetailsResponse response) {
        log.info(MSG_ADDING_IMDB_PLOT, movie.getOriginalTitle());
        Description imdbDescription = new Description();
        imdbDescription.setLanguage(Language.EN);
        imdbDescription.setText(response.plot());
        imdbDescription.setMovie(movie);
        movie.getDescriptions().add(imdbDescription);
    }

    private void sleep(long millis) {
        try {
            log.debug(MSG_SLEEPING, millis);
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn(MSG_SLEEP_INTERRUPT, e);
        }
    }
}
