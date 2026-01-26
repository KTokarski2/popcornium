package com.teg.popcornium_api.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GraphVectorSearchService {

    private final Neo4jClient neo4jClient;

    public List<VectorSearchResult> searchMoviesByEmbedding(float[] embedding, int k) {
        return neo4jClient.query("""
                CALL db.index.vector.queryNodes(
                    'movie_embedding_index',
                    $k,
                    $embedding
                )
                YIELD node, score
                RETURN node.id AS movieId, node.originalTitle AS title, score
                ORDER BY score DESC
            """)
                .bind(k).to("k")
                .bind(embedding).to("embedding")
                .fetch()
                .all()
                .stream()
                .map(record -> new VectorSearchResult(
                        (String) record.get("movieId"),
                        (String) record.get("title"),
                        ((Number) record.get("score")).doubleValue()
                ))
                .toList();
    }

    public record VectorSearchResult(String movieId, String title, double score) {}
}
