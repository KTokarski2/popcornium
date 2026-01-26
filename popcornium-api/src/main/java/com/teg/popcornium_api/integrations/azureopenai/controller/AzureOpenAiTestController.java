package com.teg.popcornium_api.integrations.azureopenai.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.teg.popcornium_api.common.model.dto.ChatQuery;
import com.teg.popcornium_api.common.model.dto.ChatResponse;
import com.teg.popcornium_api.common.service.LlmServiceImpl;
import com.teg.popcornium_api.embedding.service.api.EmbeddingService;
import com.teg.popcornium_api.rag.types.RagType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class AzureOpenAiTestController {

    private final LlmServiceImpl llmService;
    private final EmbeddingService embeddingService;

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatQuery query) throws JsonProcessingException, com.azure.json.implementation.jackson.core.JsonProcessingException {
        return ResponseEntity.ok(llmService.handle(query, RagType.GRAPH));
    }

    @GetMapping("/embedAll")
    public ResponseEntity<String> embedAll() {
        embeddingService.generateAndPersistEmbeddings();
        return ResponseEntity.ok("ez");
    }
}
