package com.teg.popcornium_api.common.repository;

import com.teg.popcornium_api.common.model.Completion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompletionRepository extends JpaRepository<Completion, String> {
}
