package com.teg.popcornium_api.integrations.azureopenai.service;

import com.teg.popcornium_api.common.service.api.AiEmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.azure.openai.AzureOpenAiEmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AzureOpenAiEmbeddingServiceImpl implements AiEmbeddingService {

    private static final String MSG_GENERATING_EMBEDDING = "Generating embedding for text of length: {}";
    private static final String MSG_GENERATING_EMBEDDINGS_BATCH = "Generating embeddings for {} texts";
    private static final String MSG_SUCCESSFULLY_GENERATED_EMBEDDING = "Successfully generated embedding";
    private static final String MSG_SUCCESSFULLY_GENERATED_EMBEDDINGS_BATCH = "Successfully generated {} embeddings";
    private static final String ERROR_FAILED_TO_GENERATE_EMBEDDING = "Failed to generate embedding";
    private static final String ERROR_TEXT_CANNOT_BE_NULL_OR_EMPTY = "Text cannot be null or empty";
    private static final String ERROR_TEXTS_CANNOT_BE_NULL_OR_EMPTY = "Texts list cannot be null or empty";

    private static final int DEFAULT_DIMENSIONS = 1536;

    private final AzureOpenAiEmbeddingModel embeddingModel;


    @Override
    public float[] embed(String text) {
        validateText(text);
        try {
            log.debug(MSG_GENERATING_EMBEDDING, text.length());
            EmbeddingResponse response = embeddingModel.embedForResponse(List.of(text));
            float[] vector = response.getResults().getFirst().getOutput();
            if (vector.length != DEFAULT_DIMENSIONS) {
                throw new IllegalStateException(
                        "Invalid embedding size: " + vector.length +
                                ", expected: " + DEFAULT_DIMENSIONS
                );
            }
            log.debug("Generated embedding of size {}", vector.length);
            return vector;

        } catch (Exception e) {
            log.error(ERROR_FAILED_TO_GENERATE_EMBEDDING, e);
            throw new RuntimeException(ERROR_FAILED_TO_GENERATE_EMBEDDING, e);
        }
    }

    @Override
    public List<List<Double>> embedBatch(List<String> texts) {
        validateTexts(texts);
        try {
            log.debug(MSG_GENERATING_EMBEDDINGS_BATCH, texts.size());
            EmbeddingResponse response = embeddingModel.embedForResponse(texts);
            List<List<Double>> embeddings = response.getResults().stream()
                    .map(embedding -> convertFloatArrayToDoubleList(embedding.getOutput()))
                    .collect(Collectors.toList());
            log.debug(MSG_SUCCESSFULLY_GENERATED_EMBEDDINGS_BATCH, embeddings.size());
            return embeddings;
        } catch (Exception e) {
            log.error(ERROR_FAILED_TO_GENERATE_EMBEDDING, e);
            throw new RuntimeException(ERROR_FAILED_TO_GENERATE_EMBEDDING, e);
        }
    }

    @Override
    public int getDimensions() {
        return DEFAULT_DIMENSIONS;
    }

    private List<Double> convertFloatArrayToDoubleList(float[] floatArray) {
        List<Double> doubleList = new ArrayList<>(floatArray.length);
        for (float value :  floatArray) {
            doubleList.add((double) value);
        }
        return doubleList;
    }

    private void validateText(String text) {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException(ERROR_TEXT_CANNOT_BE_NULL_OR_EMPTY);
        }
    }

    private void validateTexts(List<String> texts) {
        if (texts == null || texts.isEmpty()) {
            throw new IllegalArgumentException(ERROR_TEXTS_CANNOT_BE_NULL_OR_EMPTY);
        }
        texts.forEach(this::validateText);
    }
}
