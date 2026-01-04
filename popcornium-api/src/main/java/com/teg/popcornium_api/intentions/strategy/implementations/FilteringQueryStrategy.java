package com.teg.popcornium_api.intentions.strategy.implementations;

import com.teg.popcornium_api.common.model.dto.ChatMessage;
import com.teg.popcornium_api.common.model.dto.ChatRequest;
import com.teg.popcornium_api.intentions.model.Intention;
import com.teg.popcornium_api.intentions.model.LlmContext;
import com.teg.popcornium_api.intentions.strategy.QueryStrategy;
import com.teg.popcornium_api.prompts.PromptLoader;
import com.teg.popcornium_api.prompts.PromptRenderer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class FilteringQueryStrategy implements QueryStrategy {

    private final PromptLoader promptLoader;
    private final PromptRenderer promptRenderer;

    @Override
    public Intention getIntention() {
        return Intention.FILTERING;
    }

    @Override
    public ChatRequest executeStrategy(String userQuery, LlmContext context, List<ChatMessage> history) {
        String systemPrompt = promptLoader.load("filtering/system.md");
        String executionTemplate = promptLoader.load("filtering/execution.md");
        String userPrompt = promptRenderer.render(executionTemplate, Map.of(
                "query", userQuery
        ));
        return ChatRequest.builder()
                .systemPrompt(systemPrompt)
                .userMessage(userPrompt)
                .context(null)
                .conversationHistory(history)
                .temperature(0.2)
                .maxTokens(300)
                .metadata(Map.of("intention", "FILTERING"))
                .build();
    }
}
