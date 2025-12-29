package com.teg.popcornium_api.integrations.imdb.client;

import com.teg.popcornium_api.integrations.imdb.client.api.ImdbApiClient;
import com.teg.popcornium_api.integrations.imdb.config.ImdbApiProperties;
import com.teg.popcornium_api.integrations.imdb.dto.ImdbSearchResponse;
import com.teg.popcornium_api.integrations.imdb.dto.ImdbTitle;
import com.teg.popcornium_api.integrations.imdb.dto.ImdbTitleDetailsResponse;
import com.teg.popcornium_api.integrations.imdb.exception.ImdbApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImdbApiClientImpl implements ImdbApiClient {

    public static final String MSG_SEARCHING_TITLES_WITH_QUERY = "Searching titles with query: {}";
    public static final String MSG_NO_RESULTS_FOUND_FOR_QUERY = "No titles found for query: {}";
    public static final String MSG_FOUND_RESULTS_FOR_QUERY = "Found {} titles for query: {}";
    public static final String MSG_FETCHING_TITLE_DETAILS_FOR_ID = "Fetching title details for id: {}";
    public static final String MSG_NO_TITLE_FOUND_FOR_ID = "No title found for id: {}";
    public static final String MSG_SUCCESSFULLY_FETCHED_TITLE = "Successfully fetched title: {}";

    public static final String ERROR_FETCHING_TITLE_WITH_ID = "Error fetching title with id: {}";
    public static final String ERROR_FAILED_TO_FETCH_TITLE_DETAILS = "Failed to fetch title details";
    public static final String ERROR_FAILED_TO_SEARCH_QUERIES = "Failed to search queries";
    public static final String ERROR_SEARCHING_TITLES_WITH_QUERY = "Error searching titles with query: {}";

    private final RestClient restClient;
    private final ImdbApiProperties properties;

    public static final String SEARCH_TITLES_BY_QUERY_ENDPOINT = "/search/titles?query={query}&limit={limit}";
    public static final String GET_BY_TITLE_ENDPOINT = "/titles/{id}";

    @Override
    public List<ImdbTitle> searchTitles(String query, int limit) {
        try {
            log.debug(MSG_SEARCHING_TITLES_WITH_QUERY, query);
            ImdbSearchResponse response = restClient.get()
                    .uri(properties.getBaseUrl() + SEARCH_TITLES_BY_QUERY_ENDPOINT, query, limit)
                    .retrieve()
                    .body(ImdbSearchResponse.class);
            if (response == null || response.titles() == null) {
                log.warn(MSG_NO_RESULTS_FOUND_FOR_QUERY, query);
                return Collections.emptyList();
            }
            log.debug(MSG_FOUND_RESULTS_FOR_QUERY, response.titles().size(), query);
            return response.titles();
        } catch (Exception e) {
            log.error(ERROR_SEARCHING_TITLES_WITH_QUERY, query, e);
            throw new ImdbApiException(ERROR_FAILED_TO_SEARCH_QUERIES);
        }
    }

    @Override
    public Optional<ImdbTitleDetailsResponse> getTitleById(String titleId) {
        try {
            log.debug(MSG_FETCHING_TITLE_DETAILS_FOR_ID, titleId);
            ImdbTitleDetailsResponse response = restClient.get()
                    .uri(properties.getBaseUrl() + GET_BY_TITLE_ENDPOINT, titleId)
                    .retrieve()
                    .body(ImdbTitleDetailsResponse.class);
            if (response == null) {
                log.warn(MSG_NO_TITLE_FOUND_FOR_ID, titleId);
                return Optional.empty();
            }
            log.debug(MSG_SUCCESSFULLY_FETCHED_TITLE, response.primaryTitle());
            return Optional.of(response);
        } catch (RestClientException e) {
            log.error(ERROR_FETCHING_TITLE_WITH_ID, titleId, e);
            throw new ImdbApiException(ERROR_FAILED_TO_FETCH_TITLE_DETAILS, e);
        }
    }
}
