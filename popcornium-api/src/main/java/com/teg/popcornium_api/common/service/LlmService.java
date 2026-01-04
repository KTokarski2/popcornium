package com.teg.popcornium_api.common.service;

import com.teg.popcornium_api.common.model.dto.ChatMessage;
import com.teg.popcornium_api.common.model.dto.ChatQuery;
import com.teg.popcornium_api.common.model.dto.ChatRequest;
import com.teg.popcornium_api.common.model.dto.ChatResponse;
import com.teg.popcornium_api.intentions.ComplexExecutionPlanner;
import com.teg.popcornium_api.intentions.IntentionDetector;
import com.teg.popcornium_api.intentions.model.Intention;
import com.teg.popcornium_api.intentions.model.LlmContext;
import com.teg.popcornium_api.intentions.strategy.QueryStrategy;
import com.teg.popcornium_api.intentions.strategy.QueryStrategyRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LlmService {

    private final IntentionDetector intentionDetector;
    private final QueryStrategyRegistry strategyRegistry;
    private final AiChatService aiChatService;
    private final ComplexExecutionPlanner executionPlanner;

    public ChatResponse handle(ChatQuery chatQuery, List<ChatMessage> history) {
        LlmContext context = LlmContext.empty();
        context.setDetectedBaseIntention(intentionDetector.detect(chatQuery.query()));
        if (context.getDetectedBaseIntention() == Intention.COMPLEX) {
            return handleComplex(chatQuery.query(), context, history);
        }
        QueryStrategy strategy = strategyRegistry.get(context.getDetectedBaseIntention());
        ChatRequest chatRequest = strategy.executeStrategy(chatQuery.query(), context, history);
        return aiChatService.chat(chatRequest);
    }

    private ChatResponse handleComplex(String userQuery, LlmContext context, List<ChatMessage> history) {
        List<Intention> plan = executionPlanner.plan(userQuery);
        plan.forEach(step -> {
            QueryStrategy strategy = strategyRegistry.get(step);
            ChatRequest request = strategy.executeStrategy(userQuery, context, history);
            ChatResponse response = aiChatService.chat(request);
            context.putAttribute(step.name(), response.content());
        });
        QueryStrategy general = strategyRegistry.get(Intention.GENERAL);
        ChatRequest finalRequest = general.executeStrategy(userQuery, context, history);
        return aiChatService.chat(finalRequest);
    }
}
