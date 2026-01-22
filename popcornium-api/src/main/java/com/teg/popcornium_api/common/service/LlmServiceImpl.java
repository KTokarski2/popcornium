package com.teg.popcornium_api.common.service;

import com.azure.json.implementation.jackson.core.JsonProcessingException;
import com.teg.popcornium_api.common.model.Conversation;
import com.teg.popcornium_api.common.model.ConversationMessage;
import com.teg.popcornium_api.common.model.dto.*;
import com.teg.popcornium_api.common.model.types.MessageSource;
import com.teg.popcornium_api.common.repository.ConversationRepository;
import com.teg.popcornium_api.common.service.api.AiChatService;
import com.teg.popcornium_api.common.service.api.CurrentUserService;
import com.teg.popcornium_api.common.service.api.LlmService;
import com.teg.popcornium_api.intentions.ComplexExecutionPlanner;
import com.teg.popcornium_api.intentions.model.ExecutionStep;
import com.teg.popcornium_api.intentions.model.Intention;
import com.teg.popcornium_api.intentions.service.LlmContextHandler;
import com.teg.popcornium_api.intentions.strategy.QueryStrategy;
import com.teg.popcornium_api.intentions.strategy.QueryStrategyRegistry;
import com.teg.popcornium_api.rag.RagType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final ConversationRepository conversationRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional
    public ChatResponse handle(ChatQuery chatQuery, List<ChatMessage> history, RagType ragType) {
        try {
            contextHandler.createFreshContext(chatQuery.query(), ragType);
            Conversation conversation = getConversation(chatQuery);
            if (contextHandler.getBaseIntention() == Intention.COMPLEX) {
                return handleComplex(chatQuery.query(), conversation, history);
            }
            addMessageToConversation(conversation, chatQuery.query(), MessageSource.USER);
            QueryStrategy strategy = strategyRegistry.get(contextHandler.getBaseIntention());
            ChatRequest request = strategy.executeStrategy(
                    chatQuery.query(),
                    contextHandler.handleBaseIntentionContext(chatQuery.query(), contextHandler.getBaseIntention()),
                    history
            );
            LlmResponse response = aiChatService.chat(request);
            addMessageToConversation(conversation, response.content(), MessageSource.AGENT);
            conversationRepository.save(conversation);
            return ChatResponse.builder()
                    .content(response.content())
                    .conversationId(conversation.getId())
                    .build();

        } catch (JsonProcessingException | com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new RuntimeException(ERROR_JSON_PROCESSING, e);
        }
    }

    @Transactional
    protected ChatResponse handleComplex(String userQuery, Conversation conversation, List<ChatMessage> history) {
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

                LlmResponse response = aiChatService.chat(request);
                addPartialResponseToContext(step, response.content());
            }

            QueryStrategy finalStrategy = strategyRegistry.get(Intention.GENERAL);
            ChatRequest finalRequest = finalStrategy.executeStrategy(
                    userQuery,
                    contextHandler.buildFinalComplexContext(),
                    history
            );
            LlmResponse response = aiChatService.chat(finalRequest);
            addMessageToConversation(conversation, userQuery, MessageSource.USER);
            addMessageToConversation(conversation, response.content(), MessageSource.AGENT);
            conversationRepository.save(conversation);
            return ChatResponse.builder()
                    .content(response.content())
                    .conversationId(conversation.getId())
                    .build();

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

    private Conversation getConversation(ChatQuery query) {
        Conversation conversation;
        if (query.conversationId() == null) {
            conversation = new Conversation();
            conversation.setUser(currentUserService.getCurrentUser());
            return conversation;
        }
        return conversationRepository.findById(query.conversationId())
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
    }

    private void addMessageToConversation(Conversation conversation, String content, MessageSource source) {
        ConversationMessage message = new ConversationMessage();
        message.setMessageContent(content);
        message.setMessageSource(source);
        message.setConversation(conversation);
        conversation.getConversationMessages().add(message);
    }
}