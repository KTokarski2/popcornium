package com.teg.popcornium_api.embedding.service;

import com.teg.popcornium_api.common.model.Description;
import com.teg.popcornium_api.common.model.Language;
import com.teg.popcornium_api.common.model.Movie;
import com.teg.popcornium_api.common.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieDocumentService {

    private final VectorStore vectorStore;
    private final MovieRepository movieRepository;

    @EventListener
    public void handleMovieSavedEvent(MovieSavedEvent event) {
        movieRepository.findById(event.movieId())
                .ifPresentOrElse(
                        this::indexSingleMovie,
                        () -> log.error("Movie not found for indexing, ID: {}", event.movieId())
                );
    }

    private void indexSingleMovie(Movie movie) {
        if (movie == null || movie.getId() == null) {
            log.warn("Cannot index movie: entity or ID is null.");
            return;
        }

        Document document = this.mapMovieToDocument(movie);

        try {
            vectorStore.delete(List.of(document.getId()));
            vectorStore.add(List.of(document));
            log.info("Successfully indexed/updated vector for movie ID: {}", movie.getId());
        } catch (Exception e) {
            log.error("Failed to index movie ID: {}", movie.getId(), e);
        }
    }

    private Document mapMovieToDocument(Movie movie) {
        String content = createMovieContent(movie);
        Map<String, Object> metadata = createMovieMetadata(movie);
        return new Document(String.valueOf(movie.getId()), content, metadata);
    }

    private String createMovieContent(Movie movie) {
        String categories = movie.getMovieCategories().stream()
                .map(mc -> mc.getCategory().getName())
                .collect(Collectors.joining(", "));

        String actors = movie.getMovieActors().stream()
                .map(ma -> ma.getActor().getName() + " (role: " + (ma.getRoleName() != null ? ma.getRoleName() : "N/A") + ")")
                .limit(5)
                .collect(Collectors.joining("; "));

        String description = movie.getDescriptions().stream()
                .filter(d -> d.getLanguage() == Language.PL)
                .map(Description::getText)
                .findFirst()
                .orElse(movie.getOriginalTitle() + " - No detailed description available.");

        return String.format("""
                        Title: %s (%d)
                        Original Title: %s
                        Director: %s
                        Genres: %s
                        Cast: %s
                        Plot Summary: %s
                        """,
                movie.getPolishTitle(),
                movie.getProductionYear(),
                movie.getOriginalTitle(),
                movie.getDirector() != null ? movie.getDirector().getName() : "Unknown",
                categories,
                actors,
                description
        );
    }

    private Map<String, Object> createMovieMetadata(Movie movie) {
        return Map.of(
                "movie_id", movie.getId(),
                "title", movie.getPolishTitle(),
                "rating", movie.getRating() != null ? movie.getRating() : 0.0,
                "year", movie.getProductionYear()
        );
    }
}