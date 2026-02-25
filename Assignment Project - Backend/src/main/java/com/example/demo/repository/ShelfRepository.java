package com.example.demo.repository;

import com.example.demo.entity.Shelf;
import org.neo4j.driver.Driver;
import org.neo4j.driver.types.Node;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class ShelfRepository {
    private final Driver driver;

    public ShelfRepository(Driver driver) {
        this.driver = driver;
    }

    public boolean isShelfPresent(String shelfName) {
        final String cypher = """
                MATCH (s:Shelf {shelfName: $shelfName}) return s;
                """;
        try {
            var result = driver.executableQuery(cypher).withParameters(Map.of(
                    "shelfName", shelfName
            )).execute();

            return !result.records().isEmpty();
        } catch (Exception e) {
            System.err.println("Repository Error: Failed to check if shelf exists. Reason: " + e.getMessage());
            throw new RuntimeException("Database error while checking shelf existence.", e);
        }
    }

    public Optional<Shelf> createShelfAndAttach(String shelfPositionId, Shelf shelf) {
        final String cypher = """
                MATCH (sp:ShelfPosition {id: $shelfPositionId})
                WHERE NOT (sp)-[:ATTACHED]->(:Shelf)
                CREATE (s:Shelf {
                id: $id,
                shelfName: $shelfName,
                partNumber: $partNumber
                })
                CREATE (sp)-[r:ATTACHED]->(s)
                RETURN s;
                """;

        try {
            var result = driver.executableQuery(cypher).withParameters(Map.of(
                    "shelfPositionId", shelfPositionId,
                    "id", shelf.getId(),
                    "shelfName", shelf.getShelfName(),
                    "partNumber", shelf.getPartNumber()
            )).execute();

            if (result.records().isEmpty()) return Optional.empty();

            var record = result.records().getFirst();
            Node node = record.get("s").asNode();
            Shelf savedShelf = new Shelf();
            savedShelf.setId(node.get("id").asString());
            savedShelf.setShelfName(node.get("shelfName").asString());
            savedShelf.setPartNumber(node.get("partNumber").asString());

            return Optional.of(savedShelf);
        } catch (Exception e) {
            System.err.println("Repository Error: Failed to execute createShelfAndAttach method in ShelfRepository" + e.getMessage());
            throw new RuntimeException("Failed to execute createShelfAndAttach method in ShelfRepository", e);
        }
    }

    public void deleteShelf(String shelfPositionId, String shelfName) {
        final String cypher = """
                MATCH (sp:ShelfPosition {id: $shelfPositionId})-[r:ATTACHED]->(s:Shelf {shelfName: $shelfName})
                DELETE r
                REMOVE s:Shelf
                SET s:DeletedShelf
                """;

        try {
            var result = driver.executableQuery(cypher).withParameters(Map.of(
                    "shelfPositionId", shelfPositionId,
                    "shelfName", shelfName
            )).execute();

            if (result.summary().counters().relationshipsDeleted() == 0) {
                throw new IllegalArgumentException("There is no relation between ShelfPosition : " + shelfPositionId + " and Shelf : " + shelfName);
            }
        } catch (IllegalArgumentException ie) {
            throw ie;
        } catch (Exception e) {
            System.err.println("Repository Error: Failed to execute deleteShelf in ShelfRepository. Reason: " + e.getMessage());
            throw new RuntimeException("Failed to execute deleteShelf in ShelfRepository", e);
        }
    }

}
