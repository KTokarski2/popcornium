package com.teg.popcornium_api.common.service.api;

import com.teg.popcornium_api.common.model.dto.ChatMessage;
import com.teg.popcornium_api.common.model.dto.ChatQuery;
import com.teg.popcornium_api.common.model.dto.ChatResponse;

import java.util.List;

public interface LlmService {

    ChatResponse handle(ChatQuery chatQuery, List<ChatMessage> history);
}
