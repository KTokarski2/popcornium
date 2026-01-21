package com.teg.popcornium_api.common.service.api;

import com.teg.popcornium_api.common.model.dto.ConversationDetail;
import com.teg.popcornium_api.common.model.dto.ConversationSummary;

import java.util.List;

public interface ConversationService {
    List<ConversationSummary> getUserConversations();
    ConversationDetail getConversationDetail(String id);
}
