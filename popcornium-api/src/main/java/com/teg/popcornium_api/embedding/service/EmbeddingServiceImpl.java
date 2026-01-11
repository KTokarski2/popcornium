package com.teg.popcornium_api.embedding.service;

import com.teg.popcornium_api.common.model.types.ChunkType;
import com.teg.popcornium_api.common.model.Embedding;
import com.teg.popcornium_api.common.model.types.Language;
import com.teg.popcornium_api.common.model.Movie;
import com.teg.popcornium_api.common.repository.EmbeddingRepository;
import com.teg.popcornium_api.common.repository.MovieRepository;
import com.teg.popcornium_api.common.service.AiEmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmbeddingServiceImpl implements EmbeddingService{

    private final MovieRepository movieRepository;
    private final EmbeddingRepository embeddingRepository;
    private final AiEmbeddingService aiEmbeddingService;

    @Transactional
    public void generateAndPersistEmbeddings() {
        List<Movie> movies = movieRepository.findAllWithDetails();

        int embeddingsCount = 0;

        for (Movie movie : movies) {
            Map<ChunkType, String> chunks = createMovieChunks(movie);

            for (Map.Entry<ChunkType, String> entry : chunks.entrySet()) {
                ChunkType chunkType = entry.getKey();
                String content = entry.getValue();

                float[] vector = aiEmbeddingService.embed(content);

                Embedding embedding = embeddingRepository
                        .findByMovieAndChunkType(movie, chunkType)
                        .orElseGet(Embedding::new);

                embedding.setMovie(movie);
                embedding.setChunkType(chunkType);
                embedding.setChunkContent(content);
                embedding.setVectorValue(vector);

                embeddingRepository.save(embedding);
                embeddingsCount++;
            }
        }

        log.info("Generated and persisted {} embeddings for {} movies",
                embeddingsCount, movies.size());
    }

    private Map<ChunkType, String> createMovieChunks(Movie movie) {
        Map<ChunkType, String> chunks = new HashMap<>();

        String categories = movie.getMovieCategories().stream()
                .map(mc -> mc.getCategory().getName())
                .collect(Collectors.joining(", "));

        String metadataContent = String.format("""
                Title: %s (%d)
                Original Title: %s
                Production Year: %s
                Director: %s
                Categories: %s
                """,
                movie.getPolishTitle(),
                movie.getOriginalTitle(),
                movie.getProductionYear(),
                movie.getDirector() != null
                        ? movie.getDirector().getName()
                        : "Unknown",
                categories
        );

        chunks.put(ChunkType.METADATA, metadataContent);

        String actorsContent = movie.getMovieActors().stream()
                .map(ma -> ma.getActor().getName()
                        + " (role: "
                        + (ma.getRoleName() != null ? ma.getRoleName() : "N/A")
                        + ")")
                .limit(15)
                .collect(Collectors.joining("; "));

        chunks.put(ChunkType.CAST, "Cast: " + actorsContent);

        movie.getDescriptions().stream()
                .filter(d -> d.getLanguage() == Language.PL)
                .findFirst()
                .ifPresent(d ->
                        chunks.put(
                                ChunkType.PLOT_SUMMARY,
                                "Plot Summary: " + d.getText()
                        )
                );
        movie.getWikipediaArticles().stream().findFirst().ifPresent( aw ->
                chunks.put(ChunkType.WIKIPEDIA_ARTICLE,
                        "Wikipedia Article: " + aw.getText()));

        return chunks;
    }
}