package com.teg.popcornium_api.integrations.imdb.dto;

import java.util.List;

public record ImdbSearchResponse(
    List<ImdbTitle> titles
) {}
