package com.teg.popcornium_api.common.neo4jmodel;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node("Director")
@Data
@EqualsAndHashCode(callSuper = true)
public class DirectorNode extends AbstractNode {
    private String name;

    // Dynamic relationship: directors who share actors
    @Relationship(type = "COLLABORATED_WITH")
    private Set<DirectorNode> collaboratedWith = new HashSet<>();
}

