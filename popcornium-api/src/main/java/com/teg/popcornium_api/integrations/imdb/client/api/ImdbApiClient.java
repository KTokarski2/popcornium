package com.teg.popcornium_api.integrations.imdb.client.api;

import com.teg.popcornium_api.integrations.imdb.dto.ImdbTitle;
import com.teg.popcornium_api.integrations.imdb.dto.ImdbTitleDetailsResponse;

import java.util.List;
import java.util.Optional;

public interface ImdbApiClient {
    List<ImdbTitle> searchTitles(String query, int limit);
    Optional<ImdbTitleDetailsResponse> getTitleById(String titleId);
}
