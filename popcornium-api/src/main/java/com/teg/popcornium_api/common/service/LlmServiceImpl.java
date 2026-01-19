package com.teg.popcornium_api.common.service;

import com.azure.json.implementation.jackson.core.JsonProcessingException;
import com.teg.popcornium_api.common.model.dto.ChatMessage;
import com.teg.popcornium_api.common.model.dto.ChatQuery;
import com.teg.popcornium_api.common.model.dto.ChatRequest;
import com.teg.popcornium_api.common.model.dto.ChatResponse;
import com.teg.popcornium_api.common.service.api.AiChatService;
import com.teg.popcornium_api.common.service.api.LlmService;
import com.teg.popcornium_api.intentions.ComplexExecutionPlanner;
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
public class LlmServiceImpl implements LlmService {

    private static final String ERROR_JSON_PROCESSING = "JSON processing error during LLM request handling";
    private static final String ERROR_COMPLEX_JSON = "JSON processing error during complex intention execution";

    private final QueryStrategyRegistry strategyRegistry;
    private final AiChatService aiChatService;
    private final ComplexExecutionPlanner executionPlanner;
    private final LlmContextHandler contextHandler;

    @Override
    public ChatResponse handle(ChatQuery chatQuery, List<ChatMessage> history) {
        try {
            contextHandler.createFreshContext(chatQuery.query());

            if (contextHandler.getBaseIntention() == Intention.COMPLEX) {
                return handleComplex(chatQuery.query(), history);
            }

            QueryStrategy strategy = strategyRegistry.get(contextHandler.getBaseIntention());
            ChatRequest request = strategy.executeStrategy(
                    chatQuery.query(),
                    contextHandler.handleBaseIntentionContext(chatQuery.query(), contextHandler.getBaseIntention()),
                    history
            );

            return aiChatService.chat(request);

        } catch (JsonProcessingException | com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException(ERROR_JSON_PROCESSING, e);
        }
    }

    private ChatResponse handleComplex(String userQuery, List<ChatMessage> history) {
        try {
            List<ExecutionStep> plan = executionPlanner.plan(userQuery);

            for (ExecutionStep step : plan) {
                contextHandler.setCurrentStep(step);
                QueryStrategy strategy = strategyRegistry.get(step.intention());

                ChatRequest request = strategy.executeStrategy(
                        step.stepQuery(),
                        contextHandler.handleComplexIntentionContext(userQuery),
                        history
                );

                ChatResponse response = aiChatService.chat(request);
                addPartialResponseToContext(step, response.content());
            }

            QueryStrategy finalStrategy = strategyRegistry.get(Intention.GENERAL);
            ChatRequest finalRequest = finalStrategy.executeStrategy(
                    userQuery,
                    contextHandler.buildFinalComplexContext(),
                    history
            );

            return aiChatService.chat(finalRequest);

        } catch (JsonProcessingException | com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException(ERROR_COMPLEX_JSON, e);
        }
    }

    private void addPartialResponseToContext(ExecutionStep step, String response) {
        if (response == null || response.isBlank()) {
            return;
        }
        contextHandler.addPartialToContext(step.outputKey(), response);
    }
}