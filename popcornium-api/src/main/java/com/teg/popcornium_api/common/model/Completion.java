package com.teg.popcornium_api.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "completion", indexes = {
        @Index(name = "idx_completion_created", columnList = "created"),
        @Index(name = "idx_completion_model", columnList = "model")
})
@Getter
@Setter
public class Completion extends AbstractEntity {

    @Column(nullable = false, length = 100)
    private String model;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String prompt;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String response;

    @Column(name = "prompt_tokens", nullable = false)
    private Integer promptTokens;

    @Column(name = "completion_tokens", nullable = false)
    private Integer completionTokens;

    @Column(name = "total_tokens", nullable = false)
    private Integer totalTokens;

    @Column
    private Double temperature;

    @Column(name = "max_tokens")
    private Integer maxTokens;

    @Column(name = "finish_reason", length = 50)
    private String finishReason;

    @Column(columnDefinition = "TEXT")
    private String metadata;
}
