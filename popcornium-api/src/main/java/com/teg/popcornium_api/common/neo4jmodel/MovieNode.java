package com.teg.popcornium_api.common.neo4jmodel;


import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.HashSet;
import java.util.Set;

@Node("Movie")
@Data
public class MovieNode {

    @Id
    private String id;

    private String originalTitle;
    private Integer productionYear;
    private Double rating;

    @Relationship(type = "HAS_CATEGORY")
    private Set<CategoryNode> categories = new HashSet<>();

    @Relationship(type = "DIRECTED_BY", direction = Relationship.Direction.INCOMING)
    private DirectorNode director;

    @Relationship(type = "ACTED_IN")
    private Set<ActorNode> actors = new HashSet<>();
}
