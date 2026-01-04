package com.teg.popcornium_api.intentions.strategy;

import com.teg.popcornium_api.common.model.dto.ChatMessage;
import com.teg.popcornium_api.common.model.dto.ChatRequest;
import com.teg.popcornium_api.intentions.model.Intention;
import com.teg.popcornium_api.intentions.model.LlmContext;

import java.util.List;

public interface QueryStrategy {

    Intention getIntention();

    ChatRequest executeStrategy(String userQuery, LlmContext context, List<ChatMessage> history);
}
