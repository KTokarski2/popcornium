package com.teg.popcornium_api.common.service.api;

import com.teg.popcornium_api.common.model.dto.CompletionDto;

import java.util.List;

public interface CompletionService {
    List<CompletionDto> getCompletionsForUser();
    List<CompletionDto> getCompletionsForUser(String username);
}
