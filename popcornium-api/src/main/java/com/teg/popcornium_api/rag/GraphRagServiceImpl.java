package com.teg.popcornium_api.rag;

import com.teg.popcornium_api.common.model.dto.ChatRequest;
import com.teg.popcornium_api.common.model.dto.LlmResponse;
import com.teg.popcornium_api.common.service.GraphSchemaService;
import com.teg.popcornium_api.common.service.api.AiChatService;
import com.teg.popcornium_api.rag.api.GraphRagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GraphRagServiceImpl implements GraphRagService {

    private static final int MAX_ROWS = 10;

    private final AiChatService aiChatService;
    private final Neo4jClient neo4jClient;
    private final GraphSchemaService graphSchemaService;

    private static final String RAG_PROMPT = """
            You are a Neo4j Cypher expert.

                Graph schema:

                %s

                Rules:
                - Generate READ-ONLY Cypher queries only
                - Do NOT use CREATE, DELETE, MERGE, SET
                - Return human-readable fields (titles, names, years, ratings)
                - Always include LIMIT %s or lower
                - Output ONLY Cypher, no explanations
            """;

    @Override
    public String retrieveContext(String userQuery) {

        String cypher = generateCypher(userQuery);
        log.debug("GraphRAG generated Cypher:\n{}", cypher);

        if (cypher.isBlank()) {
            return "";
        }

        List<Map<String, Object>> rows;
        try {
            rows = (List<Map<String, Object>>) neo4jClient.query(cypher).fetch().all();
        } catch (Exception e) {
            log.error("GraphRAG Cypher execution failed", e);
            return "";
        }

        return formatContext(rows);
    }

    /**
     * Ask the LLM to translate a natural language question into Cypher.
     */
    private String generateCypher(String userQuery) {

        ChatRequest request = ChatRequest.builder()
                .systemPrompt(String.format(
                        RAG_PROMPT,
                        graphSchemaService.getSchemaAsString(),
                        MAX_ROWS))
                .userMessage(userQuery)
                .temperature(0.2)
                .maxTokens(800)
                .metadata(Map.of("intention", "CYPHER_GENERATION"))
                .build();


        LlmResponse response = aiChatService.chat(request);
        if (response == null || response.content() == null) {
            return "";
        }

        return sanitizeCypher(response.content());
    }

    /**
     * Basic Cypher safety net (defensive, not perfect).
     */
    private String sanitizeCypher(String cypher) {

        String cleaned = cypher.replaceAll("```cypher", "").replaceAll("```", "").trim();

        String upper = cleaned.toUpperCase(Locale.ROOT);
        if (upper.contains("CREATE ") || upper.contains("DELETE ") || upper.contains("MERGE ") || upper.contains("SET ")) {
            log.warn("Unsafe Cypher detected, ignoring:\n{}", cleaned);
            return "";
        }

        return cleaned;
    }

    /**
     * Convert Neo4j rows into LLM-friendly context.
     */
    private String formatContext(List<Map<String, Object>> rows) {

        if (rows == null || rows.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Graph database context:\n\n");

        int count = 0;
        for (Map<String, Object> row : rows) {
            if (count++ >= MAX_ROWS) {
                break;
            }

            sb.append("- ");
            row.forEach((key, value) -> {
                if (value != null) {
                    sb.append(key).append(": ").append(value).append(", ");
                }
            });

            sb.setLength(sb.length() - 2);
            sb.append("\n");
        }

        return sb.toString().trim();
    }
}