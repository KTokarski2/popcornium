package com.teg.popcornium_api.intentions.strategy;

import com.teg.popcornium_api.common.model.dto.ChatMessage;
import com.teg.popcornium_api.common.model.dto.ChatRequest;
import com.teg.popcornium_api.intentions.model.Intention;

import java.util.List;

public interface QueryStrategy {

    Intention getIntention();

    ChatRequest buildChatRequest(String userQuery, String context, List<ChatMessage> history);
}
