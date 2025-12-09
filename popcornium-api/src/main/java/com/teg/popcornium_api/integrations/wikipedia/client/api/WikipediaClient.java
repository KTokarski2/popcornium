package com.teg.popcornium_api.integrations.wikipedia.client.api;

import com.teg.popcornium_api.integrations.wikipedia.dto.WikipediaArticleDto;

import java.util.List;
import java.util.Optional;

public interface WikipediaClient {
    List<String> searchArticles(String query);
    Optional<WikipediaArticleDto> getArticleByTitle(String title);
}
