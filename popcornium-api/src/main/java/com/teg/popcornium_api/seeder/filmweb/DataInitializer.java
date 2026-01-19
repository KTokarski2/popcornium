package com.teg.popcornium_api.seeder.filmweb;

import com.teg.popcornium_api.common.repository.MovieRepository;
import com.teg.popcornium_api.seeder.filmweb.service.FileImportService;
import com.teg.popcornium_api.seeder.filmweb.service.ImdbFetcherService;
import com.teg.popcornium_api.seeder.filmweb.service.WikipediaFetcherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final FileImportService fileImportService;
    private final MovieRepository movieRepository;
    private final WikipediaFetcherService wikipediaFetcherService;
    private final ImdbFetcherService imdbFetcherService;

    @EventListener(ApplicationReadyEvent.class)
    public void seedData() {
        if (movieRepository.count() == 0) {
            log.info("Movie database is empty. Starting automated data seeding...");
            int importedCount = fileImportService.importMoviesFromFiles();
            log.info("Automated seeding finished. Imported {} movies.", importedCount);
            log.info("Fetching missing movie data from IMDB...");
            imdbFetcherService.alignMoviesData();
            int articles = wikipediaFetcherService.fetchAndSaveWikipediaArticles();
            log.info("Automated wikipedia fetch finished. Imported {} articles.", articles);
            log.info("Data feed succeed...");
        } else {
            log.info("Movie database already contains data. Skipping seeding process.");
        }
    }
}