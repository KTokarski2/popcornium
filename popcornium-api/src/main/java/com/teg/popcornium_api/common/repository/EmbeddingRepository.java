package com.teg.popcornium_api.common.repository;

import com.teg.popcornium_api.common.model.ChunkType;
import com.teg.popcornium_api.common.model.Embedding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface EmbeddingRepository extends JpaRepository<Embedding, String> {

    Embedding findByMovieIdAndChunkType(String movieId, ChunkType chunkType);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO embedding (id, movie_id, chunk_type, chunk_content, vector_value, created, modified) " +
            "VALUES (:id, :movieId, :chunkType, CAST(:chunkContent AS TEXT), CAST(:vectorValue AS vector), now(), now())",
            nativeQuery = true)
    void insertVectorNatively(
            @Param("id") String id,
            @Param("movieId") String movieId,
            @Param("chunkType") String chunkType,
            @Param("chunkContent") String chunkContent,
            @Param("vectorValue") String vectorValue
    );
}
