package com.teg.popcornium_api.common.service;

import com.teg.popcornium_api.common.model.dto.ChatMessage;
import com.teg.popcornium_api.common.model.dto.ChatQuery;
import com.teg.popcornium_api.common.model.dto.ChatRequest;
import com.teg.popcornium_api.common.model.dto.ChatResponse;
import com.teg.popcornium_api.intentions.ComplexExecutionPlanner;
import com.teg.popcornium_api.intentions.IntentionDetector;
import com.teg.popcornium_api.intentions.model.ExecutionStep;
import com.teg.popcornium_api.intentions.model.Intention;
import com.teg.popcornium_api.intentions.service.LlmContextHandler;
import com.teg.popcornium_api.intentions.strategy.QueryStrategy;
import com.teg.popcornium_api.intentions.strategy.QueryStrategyRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LlmService {

    private final QueryStrategyRegistry strategyRegistry;
    private final AiChatService aiChatService;
    private final ComplexExecutionPlanner executionPlanner;
    private final LlmContextHandler contextHandler;

    public ChatResponse handle(ChatQuery chatQuery, List<ChatMessage> history) {
        contextHandler.createFreshContext(chatQuery.query());
        if (contextHandler.getBaseIntention() == Intention.COMPLEX) {
            return handleComplex(chatQuery.query(), history);
        }
        QueryStrategy strategy = strategyRegistry.get(contextHandler.getBaseIntention());
        ChatRequest chatRequest = strategy.executeStrategy(
                chatQuery.query(),
                contextHandler.handleBaseIntentionContext(chatQuery.query()),
                history
        );
        return aiChatService.chat(chatRequest);
    }

    private ChatResponse handleComplex(String userQuery, List<ChatMessage> history) {
        List<ExecutionStep> plan = executionPlanner.plan(userQuery);
        plan.forEach(step -> {
            QueryStrategy strategy = strategyRegistry.get(step.intention());
            contextHandler.setCurrentStep(step);
            ChatRequest request = strategy.executeStrategy(
                    userQuery,
                    contextHandler.handleComplexIntentionContext(userQuery),
                    history
            );
            ChatResponse response = aiChatService.chat(request);
            addPartialResponseToContext(step, response.content());
        });
        QueryStrategy strategy = strategyRegistry.get(Intention.GENERAL);
        ChatRequest finalRequest = strategy.executeStrategy(
                userQuery,
                contextHandler.buildFinalComplexContext(),
                history
        );
        return aiChatService.chat(finalRequest);
    }

    private void addPartialResponseToContext(ExecutionStep step, String response) {
        if (!response.isBlank()) {
            this.contextHandler.addPartialToContext(step.outputKey(), response);
        }
    }
}
