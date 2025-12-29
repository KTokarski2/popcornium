package com.teg.popcornium_api.common.model;

import com.teg.popcornium_api.common.model.types.ChunkType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "embedding", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"movie_id", "chunk_type"})
})
@Getter
@Setter
public class Embedding extends AbstractEntity {

    @Column(name = "vector_value", columnDefinition = "text")
    private String vectorValue;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Column(name = "chunk_content", columnDefinition = "TEXT")
    private String chunkContent;

    @Enumerated(EnumType.STRING)
    @Column(name = "chunk_type", nullable = false)
    private ChunkType chunkType;
}
