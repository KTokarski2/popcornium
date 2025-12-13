package com.teg.popcornium_api.common.neo4jmodel;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;

@EqualsAndHashCode(callSuper = true)
@Node("Director")
@Data
public class DirectorNode extends AbstractNode {
    private String name;
}

