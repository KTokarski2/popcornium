package com.teg.popcornium_api.common.controller;

import com.teg.popcornium_api.common.model.dto.ChatQuery;
import com.teg.popcornium_api.common.model.dto.ChatResponse;
import com.teg.popcornium_api.common.model.dto.LlmResponse;
import com.teg.popcornium_api.common.service.api.LlmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/completions")
@RequiredArgsConstructor
public class ChatController {

    private final LlmService llmService;

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatQuery query) {
        return ResponseEntity.ok(llmService.handle(query, null));
    }
}
