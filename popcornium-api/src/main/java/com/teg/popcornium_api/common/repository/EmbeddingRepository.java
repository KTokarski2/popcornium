package com.teg.popcornium_api.common.repository;

import com.teg.popcornium_api.common.model.Embedding;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmbeddingRepository extends JpaRepository<Embedding,String> {
}
