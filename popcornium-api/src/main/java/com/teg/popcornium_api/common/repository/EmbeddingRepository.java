package com.teg.popcornium_api.common.repository;

import com.teg.popcornium_api.common.model.Embedding;
import com.teg.popcornium_api.common.model.Movie;
import com.teg.popcornium_api.common.model.types.ChunkType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmbeddingRepository extends JpaRepository<Embedding, String> {

    Optional<Embedding> findByMovieAndChunkType(Movie movie, ChunkType chunkType);

    @Query(value = "SELECT * FROM embedding WHERE chunk_type IN (:chunkTypes) ORDER BY " +
            "vector_value <-> CAST(:queryVector AS vector) LIMIT :limit", nativeQuery = true)
    List<Embedding> findNearestByChunkTypes(
            @Param("queryVector") float[] queryVector,
            @Param("chunkTypes") List<String> chunkTypes,
            @Param("limit") int limit
    );

}
