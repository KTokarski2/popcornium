package com.teg.popcornium_api.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GraphResetService {

    private final Neo4jClient neo4jClient;

    @Transactional("neo4jTransactionManager")
    public void resetGraph() {
        neo4jClient.query("MATCH (n) DETACH DELETE n").run();
    }
}
