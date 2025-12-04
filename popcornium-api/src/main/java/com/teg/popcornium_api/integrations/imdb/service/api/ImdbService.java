package com.teg.popcornium_api.integrations.imdb.service.api;

import com.teg.popcornium_api.integrations.imdb.dto.ImdbCrewMember;
import com.teg.popcornium_api.integrations.imdb.dto.ImdbTitle;
import com.teg.popcornium_api.integrations.imdb.dto.ImdbTitleDetailsResponse;

import java.util.List;

public interface ImdbService {
    List<ImdbTitle> searchTitles(String query, int limit);
    ImdbTitleDetailsResponse getTitleDetails(String titleId);
    List<ImdbCrewMember> findDirectors(String titleId);
}
