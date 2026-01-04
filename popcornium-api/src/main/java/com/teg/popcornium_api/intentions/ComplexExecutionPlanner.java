package com.teg.popcornium_api.intentions;

import com.teg.popcornium_api.common.model.dto.ChatRequest;
import com.teg.popcornium_api.common.service.AiChatService;
import com.teg.popcornium_api.intentions.model.Intention;
import com.teg.popcornium_api.prompts.PromptLoader;
import com.teg.popcornium_api.prompts.PromptRenderer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ComplexExecutionPlanner {

    private final PromptLoader promptLoader;
    private final PromptRenderer promptRenderer;
    private final AiChatService aiChatService;

    public List<Intention> plan(String userQuery) {
        String system = promptLoader.load("planning/system.md");
        String template = promptLoader.load("planning/execution.md");
        String prompt = promptRenderer.render(template, Map.of(
                "query", userQuery
        ));
        ChatRequest request = ChatRequest.builder()
                .systemPrompt(system)
                .userMessage(prompt)
                .temperature(0.0)
                .maxTokens(100)
                .build();
        return parseModelResponse(aiChatService.chat(request).content());
    }

    private List<Intention> parseModelResponse(String response) {
        return Arrays.stream(response.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .map(Intention::valueOf)
                .toList();
    }
}
