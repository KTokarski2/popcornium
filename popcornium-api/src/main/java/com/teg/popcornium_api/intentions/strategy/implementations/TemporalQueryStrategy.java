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
public class TemporalQueryStrategy implements QueryStrategy {

    private final PromptLoader promptLoader;
    private final PromptRenderer promptRenderer;

    @Override
    public Intention getIntention() {
        return Intention.TEMPORAL;
    }

    @Override
    public ChatRequest executeStrategy(String userQuery, LlmContext context, List<ChatMessage> history) {
        String systemPrompt = promptLoader.load("temporal/system.md");
        String executionTemplate = promptLoader.load("temporal/execution.md");
        String userPrompt = promptRenderer.render(executionTemplate, Map.of(
                "query", userQuery,
                "context", context.hasFinalContext() ?
                        context.getFinalContext() : ""
        ));
        return ChatRequest.builder()
                .systemPrompt(systemPrompt)
                .userMessage(userPrompt)
                .conversationHistory(history)
                .temperature(0.3)
                .maxTokens(400)
                .metadata(Map.of("intention", "TEMPORAL"))
                .build();
    }
}
