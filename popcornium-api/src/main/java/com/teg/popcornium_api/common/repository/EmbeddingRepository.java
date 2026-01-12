package com.teg.popcornium_api.common.repository;

import com.teg.popcornium_api.common.model.Embedding;
import com.teg.popcornium_api.common.model.Movie;
import com.teg.popcornium_api.common.model.types.ChunkType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmbeddingRepository extends JpaRepository<Embedding, String> {

    Optional<Embedding> findByMovieAndChunkType(Movie movie, ChunkType chunkType);

    @Query(value = "SELECT * FROM embeddingWHERE chunk_type IN (:chunkTypes)ORDER BY " +
            "vector_value <-> CAST(:queryVector AS vector) LIMIT :limit", nativeQuery = true)
    List<Embedding> findNearestByChunkTypes(
            @Param("queryVector") float[] queryVector,
            @Param("chunkTypes") List<String> chunkTypes,
            @Param("limit") int limit
    );

}
