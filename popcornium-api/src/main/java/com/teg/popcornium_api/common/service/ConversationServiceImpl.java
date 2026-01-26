package com.teg.popcornium_api.common.service;

import com.teg.popcornium_api.common.model.AbstractEntity;
import com.teg.popcornium_api.common.model.Conversation;
import com.teg.popcornium_api.common.model.dto.ConversationDetail;
import com.teg.popcornium_api.common.model.dto.ConversationSummary;
import com.teg.popcornium_api.common.model.dto.MessageDto;
import com.teg.popcornium_api.common.repository.ConversationRepository;
import com.teg.popcornium_api.common.service.api.ConversationService;
import com.teg.popcornium_api.common.service.api.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional(readOnly = true)
    public List<ConversationSummary> getUserConversations() {
        String userId = currentUserService.getCurrentUser().getId();
        List<Conversation> conversations = conversationRepository.findByUserIdOrderByCreatedDesc(userId);
        return conversations.stream()
                .map(this::toSummary)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ConversationDetail getConversationDetail(String id) {
        Conversation conversation = conversationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        if (!conversation.getUser().getId().equals(currentUserService.getCurrentUser().getId())) {
            throw new RuntimeException("Access denied");
        }
        return toDetail(conversation);
    }

    private ConversationSummary toSummary(Conversation conversation) {
        String title = conversation.getConversationMessages().stream()
                .findFirst()
                .map(msg -> {
                    String content = msg.getMessageContent();
                    return content.length() > 50 ? content.substring(0, 50) + "..." : content;
                })
                .orElse("New conversation");
        return new ConversationSummary(
                conversation.getId(),
                title,
                conversation.getCreated().toString(),
                conversation.getModified().toString()
        );
    }

    private ConversationDetail toDetail(Conversation conversation) {
        String title = conversation.getConversationMessages().stream()
                .findFirst()
                .map(msg -> {
                    String content = msg.getMessageContent();
                    return content.length() > 50 ? content.substring(0, 50) + "..." : content;
                })
                .orElse("New conversation");
        List<MessageDto> messages = conversation.getConversationMessages().stream()
                .sorted(Comparator.comparing(AbstractEntity::getCreated))
                .map(msg -> new MessageDto(
                        msg.getMessageContent(),
                        msg.getMessageSource(),
                        msg.getCreated().toString()
                ))
                .toList();
        return new ConversationDetail(
                conversation.getId(),
                title,
                messages
        );
    }
}
