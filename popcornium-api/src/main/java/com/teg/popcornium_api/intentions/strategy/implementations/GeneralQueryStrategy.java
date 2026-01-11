package com.teg.popcornium_api.intentions.strategy.implementations;

import com.teg.popcornium_api.common.model.dto.ChatMessage;
import com.teg.popcornium_api.common.model.dto.ChatRequest;
import com.teg.popcornium_api.intentions.model.Intention;
import com.teg.popcornium_api.intentions.model.LlmContext;
import com.teg.popcornium_api.intentions.strategy.QueryStrategy;
import com.teg.popcornium_api.prompts.PromptLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GeneralQueryStrategy implements QueryStrategy {

    private final PromptLoader promptLoader;

    @Override
    public Intention getIntention() {
        return Intention.GENERAL;
    }

    @Override
    public ChatRequest buildChatRequest(String userQuery, LlmContext context, List<ChatMessage> history) {
        return ChatRequest.builder()
                .systemPrompt(promptLoader.load("general/system.md"))
                .userMessage(userQuery)
                .context(context.hasRetrievedContext()
                    ? context.getRetrievedContext() : null)
                .temperature(0.7)
                .maxTokens(800)
                .metadata(Map.of("intention", "GENERAL"))
                .build();
    }
}
