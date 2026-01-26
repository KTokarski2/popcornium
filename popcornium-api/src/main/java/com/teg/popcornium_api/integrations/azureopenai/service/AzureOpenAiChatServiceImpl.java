package com.teg.popcornium_api.integrations.azureopenai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.teg.popcornium_api.common.model.Completion;
import com.teg.popcornium_api.common.model.dto.ChatRequest;
import com.teg.popcornium_api.common.model.dto.LlmResponse;
import com.teg.popcornium_api.common.repository.CompletionRepository;
import com.teg.popcornium_api.common.service.api.AiChatService;
import com.teg.popcornium_api.common.service.api.CurrentUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.azure.openai.AzureOpenAiChatModel;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AzureOpenAiChatServiceImpl implements AiChatService {

    private static final String MSG_SENDING_CHAT_REQUEST = "Sending chat request to Azure OpenAI";
    private static final String MSG_RECEIVED_RESPONSE = "Received response from Azure OpenAI with {} tokens";
    private static final String MSG_SAVED_COMPLETION = "Saved completion with id: {}";
    private static final String MSG_STREAMING_CHAT_REQUEST = "Streaming chat request to Azure OpenAI";
    private static final String ERROR_FAILED_TO_COMPLETE_CHAT = "Failed to complete chat request";
    private static final String ERROR_CHAT_REQUEST_CANNOT_BE_NULL = "Chat request cannot be null";
    private static final String ERROR_USER_MESSAGE_CANNOT_BE_EMPTY = "User message cannot be empty";

    private final AzureOpenAiChatModel chatModel;
    private final CompletionRepository completionRepository;
    private final ObjectMapper objectMapper;
    private final CurrentUserService currentUserService;

    @Override
    public LlmResponse chat(ChatRequest chatRequest) {
        validateRequest(chatRequest);
        try {
            log.debug(MSG_SENDING_CHAT_REQUEST);
            List<Message> messages = buildMessages(chatRequest);
            Prompt prompt = new Prompt(messages);
            org.springframework.ai.chat.model.ChatResponse response = chatModel.call(prompt);
            String content = response.getResult().getOutput().getText();
            Integer totalTokens = response.getMetadata().getUsage().getTotalTokens();
            Integer promptTokens = response.getMetadata().getUsage().getPromptTokens();
            Integer completionTokens = response.getMetadata().getUsage().getCompletionTokens();
            LlmResponse llmResponse = LlmResponse.builder()
                    .content(content)
                    .model(response.getMetadata().getModel())
                    .promptTokens(promptTokens)
                    .completionTokens(completionTokens)
                    .totalTokens(totalTokens)
                    .finishReason(response.getResult().getMetadata().getFinishReason())
                    .build();
            log.debug(MSG_RECEIVED_RESPONSE, totalTokens);
            saveCompletion(chatRequest, llmResponse);
            return llmResponse;
        } catch (Exception e) {
            log.error(ERROR_FAILED_TO_COMPLETE_CHAT, e);
            throw new RuntimeException(ERROR_FAILED_TO_COMPLETE_CHAT, e);
        }
    }

    @Override
    public Flux<String> chatStream(ChatRequest chatRequest) {
        validateRequest(chatRequest);
        try {
            log.debug(MSG_STREAMING_CHAT_REQUEST);
            List<Message> messages = buildMessages(chatRequest);
            Prompt prompt = new Prompt(messages);
            return chatModel.stream(prompt)
                    .mapNotNull(response -> response.getResult().getOutput().getText())
                    .onErrorResume(e -> {
                        log.error(ERROR_FAILED_TO_COMPLETE_CHAT, e);
                        return Flux.error(new RuntimeException(ERROR_FAILED_TO_COMPLETE_CHAT));
                    });
        } catch (Exception e) {
            log.error(ERROR_FAILED_TO_COMPLETE_CHAT, e);
            return Flux.error(new RuntimeException(ERROR_FAILED_TO_COMPLETE_CHAT, e));
        }
    }

    private List<Message> buildMessages(ChatRequest request) {
        List<Message> messages = new ArrayList<>();
        if (request.systemPrompt() != null && !request.systemPrompt().isEmpty()) {
            messages.add(new SystemMessage(request.systemPrompt()));
        }
        if (request.context() != null && !request.context().isEmpty()) {
            messages.add(new SystemMessage("CONTEXT: " + request.context()));
        }
        messages.add(new UserMessage(request.userMessage()));
        return messages;
    }

    private void saveCompletion(ChatRequest request, LlmResponse response) {
        try {
            String fullPrompt = buildFullPrompt(request);
            String metadataJson = request.metadata() != null
                    ? objectMapper.writeValueAsString(request.metadata())
                    : null;
            Completion completion = new Completion();
            completion.setModel(response.model());
            completion.setPrompt(fullPrompt);
            completion.setResponse(response.content());
            completion.setPromptTokens(response.promptTokens());
            completion.setCompletionTokens(response.completionTokens());
            completion.setTotalTokens(response.totalTokens());
            completion.setTemperature(request.temperature() != null ? request.temperature() : 0.7);
            completion.setMaxTokens(request.maxTokens() != null ? request.maxTokens() : 1000);
            completion.setFinishReason(response.finishReason());
            completion.setMetadata(metadataJson);
            completion.setUser(currentUserService.getCurrentUser());
            Completion saved = completionRepository.save(completion);
            log.debug(MSG_SAVED_COMPLETION, saved.getId());
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize metadata", e);
        } catch (Exception e) {
            log.error("Failed to save completion");
        }
    }

    private String buildFullPrompt(ChatRequest request) {
        StringBuilder prompt = new StringBuilder();

        if (request.systemPrompt() != null) {
            prompt.append("SYSTEM: ").append(request.systemPrompt()).append("\n\n");
        }

        if (request.context() != null) {
            prompt.append("CONTEXT: ").append(request.context()).append("\n\n");
        }

        prompt.append("USER: ").append(request.userMessage());

        return prompt.toString();
    }

    private void validateRequest(ChatRequest request) {
        if (request == null) {
            throw new IllegalArgumentException(ERROR_CHAT_REQUEST_CANNOT_BE_NULL);
        }
        if (request.userMessage() == null || request.userMessage().trim().isEmpty()) {
            throw new IllegalArgumentException(ERROR_USER_MESSAGE_CANNOT_BE_EMPTY);
        }
    }
}
