package com.teg.popcornium_api.common.neo4jmodel;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node("Category")
@Data
@EqualsAndHashCode(callSuper = true)
public class CategoryNode extends AbstractNode {
    private String name;

    // Optional: connect categories that appear together
    @Relationship(type = "CO_OCCURS_WITH")
    private Set<CategoryNode> coOccurring = new HashSet<>();
}