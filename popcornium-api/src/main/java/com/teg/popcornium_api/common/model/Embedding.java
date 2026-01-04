package com.teg.popcornium_api.common.model;

import com.teg.popcornium_api.common.model.types.ChunkType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "embedding", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"movie_id", "chunk_type"})
})
@Getter
@Setter
public class Embedding extends AbstractEntity {

    @JdbcTypeCode(SqlTypes.VECTOR)
    @Column(name = "vector_value", columnDefinition = "vector(1536)", nullable = false)
    private float[] vectorValue;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Column(name = "chunk_content", columnDefinition = "TEXT", nullable = false)
    private String chunkContent;

    @Enumerated(EnumType.STRING)
    @Column(name = "chunk_type", nullable = false)
    private ChunkType chunkType;
}
