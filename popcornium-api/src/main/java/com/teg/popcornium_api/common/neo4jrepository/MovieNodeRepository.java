package com.teg.popcornium_api.common.neo4jrepository;

import com.teg.popcornium_api.common.neo4jmodel.MovieNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;

public interface MovieNodeRepository extends Neo4jRepository<MovieNode, String> {

    @Query("""
        MATCH (ref:Movie {id: $movieId})
        MATCH (ref)-[:ACTED_IN]->(a:Actor)
        OPTIONAL MATCH (ref)<-[:DIRECTED_BY]-(d:Director)
        MATCH (ref)-[:HAS_CATEGORY]->(c:Category)
        MATCH (m:Movie)
        WHERE m.id <> $movieId AND (
            (m)-[:ACTED_IN]->(a) OR
            (m)<-[:DIRECTED_BY]-(d) OR
            (m)-[:HAS_CATEGORY]->(c)
        )
        RETURN m.id AS movieId,
               COUNT(DISTINCT a) * 3 + COUNT(DISTINCT d) * 5 + COUNT(DISTINCT c) AS score
        ORDER BY score DESC
        LIMIT 10
    """)
    List<GraphRecommendationProjection> findRelatedMoviesByGraphContext(String movieId);

    interface GraphRecommendationProjection {
        String getMovieId();
        Integer getScore();
    }
}