package com.teg.popcornium_api.intentions.strategy.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teg.popcornium_api.common.model.dto.ChatMessage;
import com.teg.popcornium_api.common.model.dto.ChatRequest;
import com.teg.popcornium_api.common.model.dto.ChatResponse;
import com.teg.popcornium_api.common.service.api.AiChatService;
import com.teg.popcornium_api.intentions.model.Intention;
import com.teg.popcornium_api.intentions.model.MovieFilterDto;
import com.teg.popcornium_api.intentions.model.MovieFilteringResultDto;
import com.teg.popcornium_api.intentions.service.MovieFilteringService;
import com.teg.popcornium_api.intentions.strategy.QueryStrategy;
import com.teg.popcornium_api.intentions.utils.FilteringContextBuilder;
import com.teg.popcornium_api.prompts.PromptLoader;
import com.teg.popcornium_api.prompts.PromptRenderer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FilteringQueryStrategy implements QueryStrategy {

    private final PromptLoader promptLoader;
    private final PromptRenderer promptRenderer;
    private final AiChatService aiChatService;
    private final MovieFilteringService filteringService;

    @Override
    public Intention getIntention() {
        return Intention.FILTERING;
    }

    @Override
    public ChatRequest executeStrategy(String userQuery, Optional<String> context, List<ChatMessage> history) throws JsonProcessingException, com.azure.json.implementation.jackson.core.JsonProcessingException {
        String systemPrompt = promptLoader.load("filtering/system.md");
        String executionTemplate = promptLoader.load("filtering/execution.md");
        String userPrompt = promptRenderer.render(executionTemplate, Map.of(
                "query", userQuery
        ));
        ChatRequest request = ChatRequest.builder()
                .systemPrompt(systemPrompt)
                .userMessage(userPrompt)
                .context(null)
                .conversationHistory(history)
                .temperature(0.2)
                .maxTokens(300)
                .metadata(Map.of("intention", "FILTERING"))
                .build();
        ChatResponse response = aiChatService.chat(request);
        String json = response.content();
        List<MovieFilteringResultDto> results = filteringService.filter(mapResponse(json));
        String resultsString = FilteringContextBuilder.build(results);
        return ChatRequest.builder()
                .systemPrompt(promptLoader.load("general/system.md"))
                .userMessage(userQuery)
                .context(resultsString)
                .temperature(0.7)
                .maxTokens(800)
                .metadata(Map.of("intention", "GENERAL"))
                .build();
    }

    private MovieFilterDto mapResponse(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(
                json,
                MovieFilterDto.class
        );
    }

}
