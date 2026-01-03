package com.teg.popcornium_api.rag;

import com.teg.popcornium_api.common.model.Embedding;
import com.teg.popcornium_api.common.model.Movie;
import com.teg.popcornium_api.common.model.types.ChunkType;
import com.teg.popcornium_api.common.repository.EmbeddingRepository;
import com.teg.popcornium_api.common.service.AiEmbeddingService;
import com.teg.popcornium_api.intentions.model.Intention;
import com.teg.popcornium_api.rag.api.RagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RagServiceImpl implements RagService {

    private final AiEmbeddingService aiEmbeddingService;
    private final EmbeddingRepository embeddingRepository;

    @Transactional(readOnly = true)
    @Override
    public String retrieveContext(String userQuery, Intention intention) {

        int limit = resolveLimit(intention);
        Set<ChunkType> chunkTypes = resolveChunkTypes(intention);

        List<Double> queryVector = aiEmbeddingService.embed(userQuery);

        String vectorString = queryVector.stream()
                .map(d -> String.format("%.8f", d))
                .collect(Collectors.joining(",", "[", "]"));

        List<Embedding> results =
                embeddingRepository.findNearestByChunkTypes(
                        vectorString,
                        chunkTypes.stream().map(Enum::name).toList(),
                        limit
                );

        if (results.isEmpty()) {
            return "";
        }

        // Grupowanie po Movie
        Map<Movie, List<Embedding>> byMovie = results.stream()
                .filter(e -> e.getMovie() != null)
                .collect(Collectors.groupingBy(Embedding::getMovie));

        StringBuilder context = new StringBuilder();
        context.append("KONTEKST FILMOWY:\n\n");

        for (Map.Entry<Movie, List<Embedding>> entry : byMovie.entrySet()) {
            Movie movie = entry.getKey();

            context.append("FILM: ")
                    .append(movie.getPolishTitle())
                    .append(" (")
                    .append(movie.getProductionYear())
                    .append(")\n");

            entry.getValue().forEach(e -> {
                context.append("[")
                        .append(e.getChunkType())
                        .append("] ")
                        .append(e.getChunkContent())
                        .append("\n");
            });

            context.append("\n");
        }

        return context.toString();
    }


    private int resolveLimit(Intention intention) {
        return switch (intention) {
            case AGGREGATION -> 20;
            case TEMPORAL -> 15;
            case REASONING -> 12;
            default -> 8;
        };
    }

    private Set<ChunkType> resolveChunkTypes(Intention intention) {
        return switch (intention) {
            case TEMPORAL -> Set.of(ChunkType.METADATA);
            case REASONING -> Set.of(ChunkType.PLOT_SUMMARY);
            case AGGREGATION -> Set.of(
                    ChunkType.METADATA,
                    ChunkType.PLOT_SUMMARY
            );
            default -> Set.of(
                    ChunkType.METADATA,
                    ChunkType.CAST,
                    ChunkType.PLOT_SUMMARY
            );
        };
    }
}
