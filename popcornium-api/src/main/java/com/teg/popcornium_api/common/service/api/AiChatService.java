package com.teg.popcornium_api.common.service.api;

import com.teg.popcornium_api.common.model.dto.ChatRequest;
import com.teg.popcornium_api.common.model.dto.LlmResponse;
import reactor.core.publisher.Flux;

public interface AiChatService {
    LlmResponse chat(ChatRequest chatRequest);
    Flux<String> chatStream(ChatRequest chatRequest);
}
