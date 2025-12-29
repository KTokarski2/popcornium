package com.teg.popcornium_api.common.repository;

import com.teg.popcornium_api.common.model.Language;
import com.teg.popcornium_api.common.model.WikipediaArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WikipediaArticleRepository extends JpaRepository<WikipediaArticle, String> {
    Optional<WikipediaArticle> findByMovieIdAndLanguage(String movieId, Language language);
}
