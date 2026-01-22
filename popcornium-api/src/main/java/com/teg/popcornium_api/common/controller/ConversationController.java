package com.teg.popcornium_api.common.controller;

import com.teg.popcornium_api.common.model.dto.ConversationDetail;
import com.teg.popcornium_api.common.model.dto.ConversationSummary;
import com.teg.popcornium_api.common.service.api.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;

    @GetMapping
    public ResponseEntity<List<ConversationSummary>> getUserConversations() {
        return ResponseEntity.ok(conversationService.getUserConversations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConversationDetail> getConversation(@PathVariable String id) {
        return ResponseEntity.ok(conversationService.getConversationDetail(id));
    }
}
