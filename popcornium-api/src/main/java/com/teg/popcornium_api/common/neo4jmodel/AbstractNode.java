package com.teg.popcornium_api.common.neo4jmodel;

import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;

@Data
public abstract class AbstractNode {
    @Id
    private String id; // We'll use JPA entity UUID as id
}