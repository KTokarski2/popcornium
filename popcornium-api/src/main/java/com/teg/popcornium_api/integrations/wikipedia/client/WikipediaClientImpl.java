package com.teg.popcornium_api.integrations.wikipedia.client;

import com.teg.popcornium_api.integrations.wikipedia.client.api.WikipediaClient;
import com.teg.popcornium_api.integrations.wikipedia.config.WikipediaApiProperties;
import com.teg.popcornium_api.integrations.wikipedia.dto.WikipediaArticleDto;
import com.teg.popcornium_api.integrations.wikipedia.exception.WikipediaApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class WikipediaClientImpl implements WikipediaClient {

    private static final String MSG_SEARCHING_ARTICLES_WITH_QUERY = "Searching Wikipedia articles with query: {}";
    private static final String MSG_NO_RESULTS_FOUND_FOR_QUERY = "No articles found for query: {}";
    private static final String MSG_FOUND_RESULTS_FOR_QUERY = "Found {} articles for query: {}";
    private static final String MSG_FETCHING_ARTICLE_FOR_TITLE = "Fetching article content for title: {}";
    private static final String MSG_NO_ARTICLE_FOUND_FOR_TITLE = "No article found for title: {}";
    private static final String MSG_SUCCESSFULLY_FETCHED_ARTICLE = "Successfully fetched article: {}";

    private static final String ERROR_SEARCHING_ARTICLES_WITH_QUERY = "Error searching articles with query: {}";
    private static final String ERROR_FETCHING_ARTICLE_WITH_TITLE = "Error fetching article with title: {}";
    private static final String ERROR_FAILED_TO_SEARCH_ARTICLES = "Failed to search Wikipedia articles";
    private static final String ERROR_FAILED_TO_FETCH_ARTICLE = "Failed to fetch Wikipedia article";

    private static final String CONTENT_ENDPOINT = "?action=query&prop=extracts&explaintext=true&titles={title}&format=json";
    private static final String SEARCH_ENDPOINT = "?action=query&list=search&srsearch={query}&srlimit={limit}&format=json";

    private final RestClient restClient;
    private final WikipediaApiProperties properties;

    @Override
    public List<String> searchArticles(String query) {
        try {
            log.debug(MSG_SEARCHING_ARTICLES_WITH_QUERY, query);


            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.get()
                    .uri(properties.getBaseUrl() + SEARCH_ENDPOINT, query, properties.getSearchLimit())
                    .retrieve()
                    .body(Map.class);

            if (response == null) {
                log.warn(MSG_NO_RESULTS_FOUND_FOR_QUERY, query);
                return Collections.emptyList();
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> queryResult = (Map<String, Object>) response.get("query");
            if (queryResult == null) {
                log.warn(MSG_NO_RESULTS_FOUND_FOR_QUERY, query);
                return Collections.emptyList();
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> searchResults = (List<Map<String, Object>>) queryResult.get("search");

            if (searchResults == null || searchResults.isEmpty()) {
                log.warn(MSG_NO_RESULTS_FOUND_FOR_QUERY, query);
                return Collections.emptyList();
            }

            List<String> titles = searchResults.stream()
                    .map(result -> (String) result.get("title"))
                    .filter(Objects::nonNull)
                    .toList();

            log.debug(MSG_FOUND_RESULTS_FOR_QUERY, titles.size(), query);
            return titles;

        } catch (RestClientException e) {
            log.error(ERROR_SEARCHING_ARTICLES_WITH_QUERY, query, e);
            throw new WikipediaApiException(ERROR_FAILED_TO_SEARCH_ARTICLES, e);
        }
    }

    @Override
    public Optional<WikipediaArticleDto> getArticleByTitle(String title) {
        try {
            log.debug(MSG_FETCHING_ARTICLE_FOR_TITLE, title);

            @SuppressWarnings("unchecked")
            Map<String, Object> response = restClient.get()
                    .uri(properties.getBaseUrl() + CONTENT_ENDPOINT, title)
                    .retrieve()
                    .body(Map.class);

            if (response == null) {
                log.warn(MSG_NO_ARTICLE_FOUND_FOR_TITLE, title);
                return Optional.empty();
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> query = (Map<String, Object>) response.get("query");
            if (query == null) {
                log.warn(MSG_NO_ARTICLE_FOUND_FOR_TITLE, title);
                return Optional.empty();
            }

            @SuppressWarnings("unchecked")
            Map<String, Map<String, Object>> pages = (Map<String, Map<String, Object>>) query.get("pages");
            if (pages == null || pages.isEmpty()) {
                log.warn(MSG_NO_ARTICLE_FOUND_FOR_TITLE, title);
                return Optional.empty();
            }

            Map<String, Object> page = pages.values().iterator().next();
            String articleTitle = (String) page.get("title");
            String extract = (String) page.get("extract");

            if (extract == null || extract.isEmpty()) {
                log.warn(MSG_NO_ARTICLE_FOUND_FOR_TITLE, title);
                return Optional.empty();
            }

            WikipediaArticleDto article = WikipediaArticleDto.builder()
                    .title(articleTitle)
                    .content(extract)
                    .url(buildArticleUrl(articleTitle))
                    .build();

            log.debug(MSG_SUCCESSFULLY_FETCHED_ARTICLE, articleTitle);
            return Optional.of(article);

        } catch (RestClientException e) {
            log.error(ERROR_FETCHING_ARTICLE_WITH_TITLE, title, e);
            throw new WikipediaApiException(ERROR_FAILED_TO_FETCH_ARTICLE, e);
        }
    }

    private String buildArticleUrl(String title) {
        String encodedTitle = URLEncoder.encode(title.replace(" ", "_"), StandardCharsets.UTF_8);
        return "https://pl.wikipedia.org/wiki/" + encodedTitle;
    }
}
