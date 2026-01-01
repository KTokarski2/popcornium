package com.teg.popcornium_api.integrations.azureopenai.controller;

import com.teg.popcornium_api.common.model.dto.ChatQuery;
import com.teg.popcornium_api.common.model.dto.ChatRequest;
import com.teg.popcornium_api.common.model.dto.ChatResponse;
import com.teg.popcornium_api.common.service.AiChatService;
import com.teg.popcornium_api.common.service.AiEmbeddingService;
import com.teg.popcornium_api.common.service.LlmService;
import com.teg.popcornium_api.embedding.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class AzureOpenAiTestController {

    private final LlmService llmService;
    private final EmbeddingService embeddingService;

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatQuery query) {
        return ResponseEntity.ok(llmService.handle(query, null));
    }

    @GetMapping("/embedAll")
    public ResponseEntity<String> embedAll() {
        embeddingService.generateAndPersistEmbeddings();
        return ResponseEntity.ok("ez");
    }
}
