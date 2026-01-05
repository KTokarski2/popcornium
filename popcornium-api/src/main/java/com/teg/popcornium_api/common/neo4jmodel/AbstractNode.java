package com.teg.popcornium_api.common.neo4jmodel;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;

@Getter
@Setter
public abstract class AbstractNode {
    @Id
    @GeneratedValue
    private Long neo4jId;
}