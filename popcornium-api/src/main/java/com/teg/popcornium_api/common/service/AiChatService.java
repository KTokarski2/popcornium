package com.teg.popcornium_api.common.service;

import com.teg.popcornium_api.common.model.dto.ChatRequest;
import com.teg.popcornium_api.common.model.dto.ChatResponse;
import reactor.core.publisher.Flux;

public interface AiChatService {
    ChatResponse chat(ChatRequest chatRequest);
    Flux<String> chatStream(ChatRequest chatRequest);
}
