package com.teg.popcornium_api.common.neo4jmodel;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node("Actor")
@Data
@EqualsAndHashCode(callSuper = true)
public class ActorNode extends AbstractNode {
    private String name;

    // Dynamic relationship: actors that worked together
    @Relationship(type = "WORKED_WITH")
    private Set<ActorNode> workedWith = new HashSet<>();
}


