package com.teg.popcornium_api.common.service;

import java.util.List;

public interface AiEmbeddingService {
    List<Double> embed(String text);
    List<List<Double>> embedBatch(List<String> texts);
    int getDimensions();
}
