package com.teg.popcornium_api.intentions.strategy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.teg.popcornium_api.common.model.dto.ChatMessage;
import com.teg.popcornium_api.common.model.dto.ChatRequest;
import com.teg.popcornium_api.intentions.model.Intention;

import java.util.List;
import java.util.Optional;

public interface QueryStrategy {

    Intention getIntention();

    ChatRequest executeStrategy(String userQuery, Optional<String> context) throws JsonProcessingException, com.azure.json.implementation.jackson.core.JsonProcessingException;
}
