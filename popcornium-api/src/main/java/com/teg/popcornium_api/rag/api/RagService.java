package com.teg.popcornium_api.rag.api;

import com.teg.popcornium_api.intentions.model.Intention;

public interface RagService {
    String retrieveContext(String userQuery, Intention intention);
}
