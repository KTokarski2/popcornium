package com.teg.popcornium_api.common.service;

import com.teg.popcornium_api.common.model.dto.ChatMessage;
import com.teg.popcornium_api.common.model.dto.ChatQuery;
import com.teg.popcornium_api.common.model.dto.ChatRequest;
import com.teg.popcornium_api.common.model.dto.ChatResponse;
import com.teg.popcornium_api.intentions.IntentionDetector;
import com.teg.popcornium_api.intentions.model.Intention;
import com.teg.popcornium_api.intentions.model.LlmContext;
import com.teg.popcornium_api.intentions.strategy.QueryStrategy;
import com.teg.popcornium_api.intentions.strategy.QueryStrategyRegistry;
import com.teg.popcornium_api.rag.api.RagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LlmService {

    private final IntentionDetector intentionDetector;
    private final QueryStrategyRegistry strategyRegistry;
    private final AiChatService aiChatService;
    private final RagService ragService;

    public ChatResponse handle(ChatQuery chatQuery, List<ChatMessage> history) {

        Intention intention = intentionDetector.detect(chatQuery.query());

        LlmContext context = LlmContext.empty();

        if (supportsRag(intention)) {
            String retrievedContext = ragService.retrieveContext(chatQuery.query(), intention);
            if (!retrievedContext.isBlank()) {
                context.setRetrievedContext(retrievedContext);
            }
        }

        QueryStrategy strategy = strategyRegistry.get(intention);

        ChatRequest chatRequest = strategy.buildChatRequest(
                chatQuery.query(),
                context,
                history
        );
        return aiChatService.chat(chatRequest);
    }

    private boolean supportsRag(Intention intention) {
        return switch (intention) {
            case GENERAL, REASONING, AGGREGATION, TEMPORAL -> true;
            case COUNTING, FILTERING -> false;
            default -> false;
        };
    }
}
