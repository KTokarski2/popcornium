package com.teg.popcornium_api.embedding.service;

import com.teg.popcornium_api.common.model.Description;
import com.teg.popcornium_api.common.model.Language;
import com.teg.popcornium_api.common.model.Movie;
import com.teg.popcornium_api.common.repository.EmbeddingRepository;
import com.teg.popcornium_api.common.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieDocumentService {

    private final VectorStore vectorStore;
    private final MovieRepository movieRepository;
    private final EmbeddingRepository embeddingRepository;

//    @EventListener
//    public void handleMovieSavedEvent(MovieSavedEvent event) {
//        movieRepository.findById(event.movieId())
//                .ifPresentOrElse(
//                        this::indexSingleMovie,
//                        () -> log.error("Movie not found for indexing, ID: {}", event.movieId())
//                );
//    }

    @Transactional
    public List<String> getAllMovies() {
       return movieRepository.findAllWithDetails().stream()
                .map(this::createMovieContent)
                .toList();
    }

    public void persistEmbeddings(List<Double> embeddings){
       // embeddingRepository.save()
    }


//    private void indexSingleMovie(Movie movie) {
//        if (movie == null || movie.getId() == null) {
//            log.warn("Cannot index movie: entity or ID is null.");
//            return;
//        }
//
//        Document document = this.mapMovieToDocument(movie);
//
//        try {
//            vectorStore.delete(List.of(document.getId()));
//            vectorStore.add(List.of(document));
//            log.info("Successfully indexed/updated vector for movie ID: {}", movie.getId());
//        } catch (Exception e) {
//            log.error("Failed to index movie ID: {}", movie.getId(), e);
//        }
//    }

//    private Document mapMovieToDocument(Movie movie) {
//        String content = createMovieContent(movie);
//        Map<String, Object> metadata = createMovieMetadata(movie);
//        return new Document(String.valueOf(movie.getId()), content, metadata);
//    }

    private String createMovieContent(Movie movie) {
        //TODO: uncomment once the categories are added;
//        String categories = movie.getMovieCategories().stream()
//                .map(mc -> mc.getCategory().getName())
//                .collect(Collectors.joining(", "));

        String actors = movie.getMovieActors().stream()
                .map(ma -> ma.getActor().getName() + " (role: " + (ma.getRoleName() != null ? ma.getRoleName() : "N/A") + ")")
                .limit(15)
                .collect(Collectors.joining("; "));

        List<String> descriptions = movie.getDescriptions().stream()
                .filter(d -> d.getLanguage() == Language.PL)
                .map(Description::getText).limit(3).toList();

        
        //tablica opisow
        //dodac artykul z wiki

        return String.format("""
                        Title: %s (%d)
                        Original Title: %s
                        Director: %s
                        Cast: %s
                        Plot Summaries: %s
                        """,
                movie.getPolishTitle(),
                movie.getProductionYear(),
                movie.getOriginalTitle(),
                movie.getDirector() != null ? movie.getDirector().getName() : "Unknown",
//                categories,
                actors,
                descriptions
        );
    }

//    private Map<String, Object> createMovieMetadata(Movie movie) {
//        return Map.of(
//                "movie_id", movie.getId(),
//                "title", movie.getPolishTitle(),
//                "rating", movie.getRating() != null ? movie.getRating() : 0.0,
//                "year", movie.getProductionYear()
//        );
//    }
}