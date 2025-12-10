package com.teg.popcornium_api.integrations.imdb.service;

import com.teg.popcornium_api.integrations.imdb.client.api.ImdbApiClient;
import com.teg.popcornium_api.integrations.imdb.dto.ImdbCrewMember;
import com.teg.popcornium_api.integrations.imdb.dto.ImdbTitle;
import com.teg.popcornium_api.integrations.imdb.dto.ImdbTitleDetailsResponse;
import com.teg.popcornium_api.integrations.imdb.exception.TitleNotFoundException;
import com.teg.popcornium_api.integrations.imdb.service.api.ImdbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImdbServiceImpl implements ImdbService {

    public static final String MSG_SEARCHING_FOR_TITLES_WITH_QUERY = "Searching for titles with query: {}";
    public static final String MSG_FETCHING_DETAILS_FOR_TITLE = "Fetching details for title: {}";
    public static final String MSG_FETCHING_DIRECTORS_FOR_TITLE = "Fetching directors for title: {}";
    public static final String MSG_NO_DIRECTORS_FOUND_FOR_TITLE = "No directors found for title: {}";

    public static final String ERROR_TITLE_WITH_ID_S_NOT_FOUND = "Title with id '%s' not found";
    public static final String ERROR_SEARCH_QUERY_CANNOT_BE_EMPTY = "Search query cannot be empty";
    public static final String ERROR_TITLE_ID_CANNOT_BE_EMPTY = "Title id cannot be empty";
    public static final String ERROR_LIMIT_MUST_BE_BETWEEN_1_AND_100 = "Limit must be between 1 and 100";

    private final ImdbApiClient imdbApiClient;

    @Override
    public List<ImdbTitle> searchTitles(String query, int limit) {
        validateSearchQuery(query);
        validateLimit(limit);
        log.info(MSG_SEARCHING_FOR_TITLES_WITH_QUERY, query);
        return imdbApiClient.searchTitles(query, limit);
    }

    @Override
    public ImdbTitleDetailsResponse getTitleDetails(String titleId) {
        validateTitleId(titleId);
        log.info(MSG_FETCHING_DETAILS_FOR_TITLE, titleId);
        return imdbApiClient.getTitleById(titleId)
                .orElseThrow(() -> new TitleNotFoundException(
                        String.format(ERROR_TITLE_WITH_ID_S_NOT_FOUND, titleId)
                ));
    }

    @Override
    public List<ImdbCrewMember> findDirectors(String titleId) {
        validateTitleId(titleId);
        log.info(MSG_FETCHING_DIRECTORS_FOR_TITLE, titleId);
        ImdbTitleDetailsResponse titleDetails = getTitleDetails(titleId);
        if (titleDetails.directors() == null || titleDetails.directors().isEmpty()) {
            log.warn(MSG_NO_DIRECTORS_FOUND_FOR_TITLE, titleId);
            return Collections.emptyList();
        }
        return titleDetails.directors();
    }

    private void validateSearchQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException(ERROR_SEARCH_QUERY_CANNOT_BE_EMPTY);
        }
    }

    private void validateLimit(int limit) {
        if (limit < 1 || limit > 100) {
            throw new IllegalArgumentException(ERROR_LIMIT_MUST_BE_BETWEEN_1_AND_100);
        }
    }

    private void validateTitleId(String titleId) {
        if (titleId == null || titleId.trim().isEmpty()) {
            throw new IllegalArgumentException(ERROR_TITLE_ID_CANNOT_BE_EMPTY);
        }
    }
}
