package com.teg.popcornium_api.common.neo4jmodel;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Language")
@Data
@EqualsAndHashCode(callSuper = true)
public class LanguageNode extends AbstractNode {
    private String code;
    private String fullName;
}