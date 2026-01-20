package com.teg.popcornium_api.common.service;

import com.teg.popcornium_api.common.model.Completion;
import com.teg.popcornium_api.common.model.User;
import com.teg.popcornium_api.common.model.dto.CompletionDto;
import com.teg.popcornium_api.common.repository.UserRepository;
import com.teg.popcornium_api.common.service.api.CompletionService;
import com.teg.popcornium_api.common.service.api.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompletionServiceImpl implements CompletionService {

    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional(readOnly = true)
    public List<CompletionDto> getCompletionsForUser(){
        User user = currentUserService.getCurrentUser();
        return mapCompletions(user);
    }

    @Override
    public List<CompletionDto> getCompletionsForUser(String username) {
        User user = userRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapCompletions(user);

    }

    private List<CompletionDto> mapCompletions(User user) {
        return user.getCompletions().stream()
                .map(this::mapCompletionDto)
                .collect(Collectors.toList());
    }

    private CompletionDto mapCompletionDto(Completion c) {
        return new CompletionDto(
                c.getId(),
                c.getModel(),
                c.getPrompt(),
                c.getResponse(),
                c.getPromptTokens(),
                c.getCompletionTokens(),
                c.getTotalTokens(),
                c.getTemperature(),
                c.getMaxTokens(),
                c.getFinishReason(),
                c.getMetadata()
        );
    }
}
