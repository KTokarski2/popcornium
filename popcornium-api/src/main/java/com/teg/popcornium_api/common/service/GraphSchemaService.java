package com.teg.popcornium_api.common.service;

import lombok.RequiredArgsConstructor;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Session;
import org.neo4j.driver.Value;
import org.neo4j.driver.Values;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GraphSchemaService {

    private final Driver driver;

    public String getSchemaAsString() {
        StringBuilder sb = new StringBuilder("Schemat bazy Neo4j:\n");

        try (Session session = driver.session()) {
            appendNodesSection(session, sb);
            appendRelationshipsSection(session, sb);
        }

        return sb.toString();
    }

    private void appendNodesSection(Session session, StringBuilder sb) {
        List<String> labels = getLabels(session);

        for (String label : labels) {
            List<String> props = getPropertiesForLabel(session, label);
            sb.append("- (:").append(label).append(" {")
                    .append(String.join(", ", props))
                    .append("}\n");
        }
    }

    private void appendRelationshipsSection(Session session, StringBuilder sb) {
        sb.append("Relacje:\n");

        List<String> relTypes = getRelationshipTypes(session);

        for (String type : relTypes) {
            String line = getRelationshipLine(session, type);
            if (line != null) {
                sb.append(line).append("\n");
            }
        }
    }

    private List<String> getLabels(Session session) {
        return session.run("CALL db.labels() YIELD label RETURN label")
                .list(r -> r.get("label").asString());
    }

    private List<String> getPropertiesForLabel(Session session, String label) {
        return session.run("""
                CALL db.schema.nodeTypeProperties()
                YIELD nodeLabels, propertyName
                WHERE $label IN nodeLabels
                RETURN collect(distinct propertyName) AS props
                """, Values.parameters("label", label))
                .list(r -> r.get("props").asList(Value::asString))
                .stream().findFirst().orElse(List.of());
    }

    private List<String> getRelationshipTypes(Session session) {
        return session.run("CALL db.relationshipTypes() YIELD relationshipType RETURN relationshipType")
                .list(r -> r.get("relationshipType").asString());
    }

    private String getRelationshipLine(Session session, String type) {
        return session.run("""
        MATCH ()-[r:%s]->()
        RETURN 
            labels(startNode(r))[0] AS source,
            labels(endNode(r))[0] AS target
        LIMIT 1
        """.formatted(type.replaceAll("[^a-zA-Z0-9_]", "")))   // podstawowe zabezpieczenie przed injection
                .list(r -> {
                    String source = r.get("source").asString(null);
                    String target = r.get("target").asString(null);
                    if (source == null || target == null) {
                        return "- [:" + type + "] (kierunek nieokreślony)";
                    }
                    return "- (:" + source + ")-[:" + type + "]->(:" + target + ")";
                })
                .stream().findFirst()
                .orElse("- [:" + type + "] (brak relacji w bazie)");
    }
}
