package com.teg.popcornium_api.common.service;

import com.teg.popcornium_api.common.neo4jrepository.MovieNodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GraphQueryService {

    private final MovieNodeRepository movieNodeRepository;
    private final Neo4jClient neo4jClient;

    // -------------------------
    // Graph-only queries
    // -------------------------

    /**
     * Returns top related movies based on shared actors/director/categories.
     * Delegates to MovieNodeRepository projection.
     */
    public List<MovieNodeRepository.GraphRecommendationProjection> getRelatedMovies(String movieId) {
        return movieNodeRepository.findRelatedMoviesByGraphContext(movieId);
    }

    /**
     * Find movies by actor name.
     */
    public List<MovieSummary> findMoviesByActor(String actorName, int limit) {
        var rows = neo4jClient.query("""
                MATCH (a:Actor {name: $actorName})<-[:ACTED_IN]-(m:Movie)
                RETURN m.id AS movieId, m.originalTitle AS title, m.productionYear AS year, m.rating AS rating
                ORDER BY m.productionYear DESC
                LIMIT $limit
                """)
                .bind(actorName).to("actorName")
                .bind(limit).to("limit")
                .fetch().all();

        return rows.stream().map(this::toMovieSummary).collect(Collectors.toList());
    }

    /**
     * Find movies by category name.
     */
    public List<MovieSummary> findMoviesByCategory(String categoryName, int limit) {
        var rows = neo4jClient.query("""
                MATCH (c:Category {name: $categoryName})<-[:HAS_CATEGORY]-(m:Movie)
                RETURN m.id AS movieId, m.originalTitle AS title, m.productionYear AS year, m.rating AS rating
                ORDER BY m.rating DESC NULLS LAST
                LIMIT $limit
                """)
                .bind(categoryName).to("categoryName")
                .bind(limit).to("limit")
                .fetch().all();

        return rows.stream().map(this::toMovieSummary).collect(Collectors.toList());
    }

    /**
     * Find movies by director name.
     */
    public List<MovieSummary> findMoviesByDirector(String directorName, int limit) {
        var rows = neo4jClient.query("""
                MATCH (d:Director {name: $directorName})-[:DIRECTED_BY]->(m:Movie)
                RETURN m.id AS movieId, m.originalTitle AS title, m.productionYear AS year, m.rating AS rating
                ORDER BY m.productionYear DESC
                LIMIT $limit
                """)
                .bind(directorName).to("directorName")
                .bind(limit).to("limit")
                .fetch().all();

        return rows.stream().map(this::toMovieSummary).collect(Collectors.toList());
    }

    /**
     * Top trending / popular movies by rating and actor count.
     */
    public List<MovieSummary> trendingMovies(int limit) {
        var rows = neo4jClient.query("""
                MATCH (m:Movie)
                RETURN m.id AS movieId, m.originalTitle AS title, m.rating AS rating, size((m)-[:ACTED_IN]->()) AS actorCount
                ORDER BY m.rating DESC NULLS LAST, actorCount DESC
                LIMIT $limit
                """)
                .bind(limit).to("limit")
                .fetch().all();

        return rows.stream().map(this::toMovieSummary).collect(Collectors.toList());
    }

    // -------------------------
    // Vector search
    // -------------------------

    /**
     * Pure vector (embedding) search using Neo4j's vector index.
     * embedding: list of Doubles (embedding vector)
     */
    public List<VectorSearchResult> vectorSearch(List<Double> embedding, int k) {
        var rows = neo4jClient.query("""
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
                .fetch().all();

        return rows.stream().map(this::toVectorResult).collect(Collectors.toList());
    }

    // -------------------------
    // Hybrid: vector + graph overlap
    // -------------------------

    /**
     * Hybrid search: combine vector similarity with graph overlap (shared actors/director/categories)
     *
     * @param embedding       query embedding
     * @param movieId         reference movie id to compute shared relations against (nullable).
     *                        If null, hybrid reduces to vector-only ranking.
     * @param k               number of candidates to fetch from vector index (and final limit)
     * @param actorWeight     weight to multiply with shared actor count
     * @param directorWeight  weight to multiply with shared director count
     * @param categoryWeight  weight to multiply with shared category count
     */
    public List<HybridResult> hybridSearch(List<Double> embedding,
                                           String movieId,
                                           int k,
                                           double actorWeight,
                                           double directorWeight,
                                           double categoryWeight) {

        // If movieId is null, we can return pure vector results mapped to HybridResult.
        if (movieId == null || movieId.isBlank()) {
            return vectorSearch(embedding, k).stream()
                    .map(v -> new HybridResult(v.movieId(), v.title(), v.score(), 0, 0, 0, v.score()))
                    .collect(Collectors.toList());
        }

        String cypher = """
                CALL db.index.vector.queryNodes(
                  'movie_embedding_index',
                  $k,
                  $embedding
                )
                YIELD node, score
                MATCH (cand:Movie {id: node.id})
                MATCH (ref:Movie {id: $movieId})
                OPTIONAL MATCH (ref)-[:ACTED_IN]->(a:Actor)<-[:ACTED_IN]-(cand)
                OPTIONAL MATCH (ref)<-[:DIRECTED_BY]-(d:Director)-[:DIRECTED_BY]->(cand)
                OPTIONAL MATCH (ref)-[:HAS_CATEGORY]->(c:Category)<-[:HAS_CATEGORY]-(cand)
                WITH cand, score,
                     COUNT(DISTINCT a) AS sharedActors,
                     COUNT(DISTINCT d) AS sharedDirectors,
                     COUNT(DISTINCT c) AS sharedCategories
                RETURN cand.id AS movieId,
                       cand.originalTitle AS title,
                       score AS vectorScore,
                       sharedActors, sharedDirectors, sharedCategories,
                       (score + ($actorWeight * sharedActors) + ($directorWeight * sharedDirectors) + ($categoryWeight * sharedCategories)) AS combinedScore
                ORDER BY combinedScore DESC
                LIMIT $k
                """;

        var rows = neo4jClient.query(cypher)
                .bind(k).to("k")
                .bind(embedding).to("embedding")
                .bind(movieId).to("movieId")
                .bind(actorWeight).to("actorWeight")
                .bind(directorWeight).to("directorWeight")
                .bind(categoryWeight).to("categoryWeight")
                .fetch().all();

        return rows.stream().map(this::toHybridResult).collect(Collectors.toList());
    }

    // -------------------------
    // Mappers & DTOs
    // -------------------------

    private MovieSummary toMovieSummary(Map<String, Object> row) {
        return new MovieSummary(
                (String) row.get("movieId"),
                (String) row.get("title"),
                row.get("year") != null ? ((Number) row.get("year")).intValue() : null,
                row.get("rating") != null ? ((Number) row.get("rating")).doubleValue() : null
        );
    }

    private VectorSearchResult toVectorResult(Map<String, Object> row) {
        return new VectorSearchResult(
                (String) row.get("movieId"),
                (String) row.get("title"),
                ((Number) row.get("score")).doubleValue()
        );
    }

    private HybridResult toHybridResult(Map<String, Object> row) {
        double vectorScore = row.get("vectorScore") != null ? ((Number) row.get("vectorScore")).doubleValue() : 0.0;
        int sharedActors = ((Number) row.getOrDefault("sharedActors", 0)).intValue();
        int sharedDirectors = ((Number) row.getOrDefault("sharedDirectors", 0)).intValue();
        int sharedCategories = ((Number) row.getOrDefault("sharedCategories", 0)).intValue();
        double combined = row.get("combinedScore") != null ? ((Number) row.get("combinedScore")).doubleValue() : 0.0;

        return new HybridResult(
                (String) row.get("movieId"),
                (String) row.get("title"),
                vectorScore,
                sharedActors,
                sharedDirectors,
                sharedCategories,
                combined
        );
    }

    public record MovieSummary(String movieId, String title, Integer year, Double rating) {}
    public record VectorSearchResult(String movieId, String title, double score) {}
    public record HybridResult(String movieId, String title, double vectorScore,
                               int sharedActors, int sharedDirectors, int sharedCategories,
                               double combinedScore) {}
}
