package com.teg.popcornium_api.intentions;

import com.teg.popcornium_api.common.model.dto.ChatRequest;
import com.teg.popcornium_api.common.service.api.AiChatService;
import com.teg.popcornium_api.intentions.model.ExecutionStep;
import com.teg.popcornium_api.prompts.PromptLoader;
import com.teg.popcornium_api.prompts.PromptRenderer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ComplexExecutionPlanner {

    private final PromptLoader promptLoader;
    private final PromptRenderer promptRenderer;
    private final AiChatService aiChatService;
    private final ExecutionPlanParser planParser;

    public List<ExecutionStep> plan(String userQuery) {
        String system = promptLoader.load("planning/system.md");
        String template = promptLoader.load("planning/execution.md");
        String prompt = promptRenderer.render(template, Map.of(
                "query", userQuery
        ));
        ChatRequest request = ChatRequest.builder()
                .systemPrompt(system)
                .userMessage(prompt)
                .temperature(0.0)
                .maxTokens(300)
                .build();
        String response = aiChatService.chat(request).content();
        return planParser.parse(response);
    }
}
