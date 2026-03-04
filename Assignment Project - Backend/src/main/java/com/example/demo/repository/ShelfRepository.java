package com.example.demo.repository;

import com.example.demo.DTO.UpdateShelf;
import com.example.demo.entity.Shelf;
import com.example.demo.exception.customExceptions.DatabaseOperationException;
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
            System.err.println("Repository Error: Failed to execute isShelfPresent method. Reason: " + e.getMessage());
            throw new DatabaseOperationException("Database error occurred while checking whether shelf is present in database or not.");
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
            System.err.println("Repository Error: Failed to execute createShelfAndAttach method. Reason: " + e.getMessage());
            throw new DatabaseOperationException("Database error occurred while creating shelf.");
        }
    }

    public void deleteShelf(String shelfName) {
        final String cypher = """
                MATCH ()-[r:ATTACHED]->(s:Shelf {shelfName: $shelfName})
                DELETE r
                REMOVE s:Shelf
                SET s:DeletedShelf
                """;

        try {
            var result = driver.executableQuery(cypher).withParameters(Map.of(
                    "shelfName", shelfName
            )).execute();
        } catch (Exception e) {
            System.err.println("Repository Error: Failed to execute deleteShelf method. Reason: " + e.getMessage());
            throw new DatabaseOperationException("Database error occurred while deleting shelf.");
        }
    }

    public Optional<Shelf> updateShelf(UpdateShelf dto) {
        final String cypher = """
                MATCH (s:Shelf {shelfName: $previousShelfName})
                SET s.shelfName = $newShelfName, s.partNumber = $newPartNumber
                RETURN s;
                """;

        try {
            var result = driver.executableQuery(cypher).withParameters(Map.of(
                    "previousShelfName", dto.getPreviousShelfName(),
                    "newShelfName", dto.getNewShelfName(),
                    "newPartNumber", dto.getNewPartNumber()
            )).execute();

            if (result.records().isEmpty()) return Optional.empty();

            var record = result.records().getFirst();
            Node node = record.get("s").asNode();
            Shelf updatedShelf = new Shelf();
            updatedShelf.setId(node.get("id").asString());
            updatedShelf.setShelfName(node.get("shelfName").asString());
            updatedShelf.setPartNumber(node.get("partNumber").asString());

            return Optional.of(updatedShelf);
        } catch (Exception e) {
            System.err.println("Repository Error: Failed to execute updateShelf method. Reason: " + e.getMessage());
            throw new DatabaseOperationException("Database error occurred while updating shelf.");
        }
    }

}
