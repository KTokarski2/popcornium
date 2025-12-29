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
public class EmbeddingService {

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

                List<Double> vectorList = aiEmbeddingService.embed(content);
                persistEmbedding(movie, chunkType, content, vectorList);
                embeddingsCount++;
            }
        }
        log.info("Successfully generated and persisted {} embeddings for {} movies.", embeddingsCount, movies.size());
    }

    private void persistEmbedding(Movie movie, ChunkType chunkType, String chunkContent, List<Double> vectorList) {
        String vectorString = vectorList.stream()
                .map(d -> String.format("%.8f", d))
                .collect(Collectors.joining(",", "[", "]"));

        Embedding existingEmbedding = embeddingRepository.findByMovieIdAndChunkType(movie.getId(), chunkType);

        if (existingEmbedding != null) {
            existingEmbedding.setVectorValue(vectorString);
            existingEmbedding.setChunkContent(chunkContent);
            existingEmbedding.setMovie(movie);
            embeddingRepository.save(existingEmbedding);
        } else {
            embeddingRepository.insertVectorNatively(
                    java.util.UUID.randomUUID().toString(),
                    movie.getId(),
                    chunkType.name(),
                    chunkContent,
                    vectorString
            );
        }
    }

    private Map<ChunkType, String> createMovieChunks(Movie movie) {
        Map<ChunkType, String> chunks = new HashMap<>();

//        String categories = movie.getMovieCategories().stream()
//                .map(mc -> mc.getCategory().getName())
//                .collect(Collectors.joining(", "));

        String metadataContent = String.format("""
                        Title: %s (%d)
                        Original Title: %s
                        Director: %s
                        """,
                movie.getPolishTitle(),
                movie.getProductionYear(),
                movie.getOriginalTitle(),
                movie.getDirector() != null ? movie.getDirector().getName() : "Unknown"
//                categories
        );
        chunks.put(ChunkType.METADATA, metadataContent);

        String actorsContent = movie.getMovieActors().stream()
                .map(ma -> ma.getActor().getName() + " (role: " + (ma.getRoleName() != null ? ma.getRoleName() : "N/A") + ")")
                .limit(15)
                .collect(Collectors.joining("; "));

        chunks.put(ChunkType.CAST, "Cast: " + actorsContent);

        movie.getDescriptions().stream()
                .filter(d -> d.getLanguage() == Language.PL)
                .findFirst()
                .ifPresent(description -> {
                    chunks.put(ChunkType.PLOT_SUMMARY, "Plot Summary: " + description.getText());
                });
        return chunks;
    }
}


