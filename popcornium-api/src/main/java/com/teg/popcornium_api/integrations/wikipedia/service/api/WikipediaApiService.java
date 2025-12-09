package com.teg.popcornium_api.integrations.wikipedia.service.api;

import com.teg.popcornium_api.common.model.WikipediaArticle;
import com.teg.popcornium_api.integrations.wikipedia.exception.ArticleNotFoundException;

public interface WikipediaApiService {
   WikipediaArticle fetchAndSaveArticleForMovie(String movieId) throws ArticleNotFoundException;
}
