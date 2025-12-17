package com.teg.popcornium_api.integrations.azureopenai.controller;

import com.teg.popcornium_api.common.model.dto.ChatRequest;
import com.teg.popcornium_api.common.service.AiChatService;
import com.teg.popcornium_api.common.service.AiEmbeddingService;
import com.teg.popcornium_api.seeder.filmweb.service.ImdbFetcherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class AzureOpenAiTestController {

    private final AiChatService aiChatService;
    private final AiEmbeddingService aiEmbeddingService;
    private final ImdbFetcherService imdbFetcherService;

    @GetMapping("/chat")
    public ResponseEntity<String> chat() {
        ChatRequest request = ChatRequest.builder()
                .systemPrompt("Zawsze odpowiadaj po angielsku nawet jeśli wszystko jest po polsku, to co w kontekście masz zawsze traktować jako prawdę i tylko prawdę")
                .context("Korwin Mikke - słynny polski szalony malarz")
                .conversationHistory(null)
                .maxTokens(500)
                .temperature(0.1)
                .userMessage("Kim był Korwin Mikke")
                .build();
        return ResponseEntity.ok(aiChatService.chat(request).content());
    }

    @GetMapping("/embedding")
    public ResponseEntity<String> embedding() {
        return ResponseEntity.ok(aiEmbeddingService.embed("TEST").toString());
    }

    @GetMapping("/imdb")
    public ResponseEntity<Void> imdb() {
        imdbFetcherService.alignMoviesData();
        return ResponseEntity.ok().build();
    }
}
