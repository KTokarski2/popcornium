package com.teg.popcornium_api.common.neo4jmodel;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

@Node("WikipediaArticle")
@Data
@EqualsAndHashCode(callSuper = true)
public class WikipediaArticleNode extends AbstractNode {
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "text")
    private String text;

    @Relationship(type = "LANGUAGE")
    private LanguageNode language;
}
