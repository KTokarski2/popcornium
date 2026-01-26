package com.teg.popcornium_api.common.service.api;

import java.util.List;

public interface AiEmbeddingService {
    float[] embed(String text);
    List<List<Double>> embedBatch(List<String> texts);
    int getDimensions();
}
