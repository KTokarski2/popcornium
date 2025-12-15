package com.teg.popcornium_api.common.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "embedding")
@Getter
@Setter
public class Embedding extends AbstractEntity {

    @Column(name = "embedding")
    private String embedding;
}
