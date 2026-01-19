package com.teg.popcornium_api.intentions;

import com.teg.popcornium_api.common.model.dto.ChatRequest;
import com.teg.popcornium_api.common.service.api.AiChatService;
import com.teg.popcornium_api.intentions.model.Intention;
import com.teg.popcornium_api.prompts.PromptLoader;
import com.teg.popcornium_api.prompts.PromptRenderer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class IntentionDetector {
    private final PromptLoader promptLoader;
    private final PromptRenderer promptRenderer;
    private final AiChatService aiChatService;

    public Intention detect(String userQuery) {
        String systemPrompt = promptLoader.load("detection/system.md");
        String template = promptLoader.load("detection/execution.md");
        String prompt = promptRenderer.render(template, Map.of(
                "question", userQuery
        ));

        ChatRequest request = ChatRequest.builder()
                .systemPrompt(systemPrompt)
                .userMessage(prompt)
                .temperature(0.0)
                .maxTokens(10)
                .build();
        String response = aiChatService.chat(request).content();
        try {
            return Intention.valueOf(response.trim().toUpperCase());
        } catch (Exception e) {
            return Intention.GENERAL;
        }
    }
}
