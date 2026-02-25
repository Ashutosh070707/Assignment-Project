package com.example.demo.repository;

import com.example.demo.entity.ShelfPosition;
import org.neo4j.driver.Driver;
import org.neo4j.driver.types.Node;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class ShelfPositionRepository {
    private final Driver driver;

    public ShelfPositionRepository(Driver driver) {
        this.driver = driver;
    }

    public boolean isShelfPositionPresent(String id) {
        final String cypher = """
                MATCH (sp:ShelfPosition {id: $id})
                RETURN sp;
                """;

        try {
            var result = driver.executableQuery(cypher).withParameters(Map.of(
                    "id", id
            )).execute();

            return !result.records().isEmpty();
        } catch (Exception e) {
            System.err.println("Repository Error: Failed to execute isShelfPositionPresent method in ShelfPositionRepository. Reason: " + e.getMessage());
            throw new RuntimeException("Failed to execute isShelfPositionPresent method in ShelfPositionRepository", e);
        }
    }

    public Optional<ShelfPosition> createShelfPositionAndAttach(ShelfPosition shelfPosition) {
        final String cypher = """
                MATCH (d:Device {
                id: $deviceId
                })
                WITH d
                MERGE (sp:ShelfPosition {
                    id: $id,
                    deviceId: $deviceId
                })
                MERGE (d)-[r:HAS]->(sp)
                RETURN sp;
                """;

        try {
            var result = driver.executableQuery(cypher).withParameters(Map.of(
                    "deviceId", shelfPosition.getDeviceId(),
                    "id", shelfPosition.getId()
            )).execute();

            if (result.records().isEmpty()) {
                return Optional.empty();
            }

            var record = result.records().getFirst();
            Node node = record.get("sp").asNode();
            ShelfPosition savedShelfPosition = new ShelfPosition();
            savedShelfPosition.setId(node.get("id").asString());
            savedShelfPosition.setDeviceId(node.get("deviceId").asString());

            return Optional.of(savedShelfPosition);
        } catch (Exception e) {
            System.err.println("Repository Error: Failed to execute Cypher in createShelfPositionAndAttach function in ShelfPositionRepository" + e.getMessage());
            throw new RuntimeException("Failed to execute Cypher in createShelfPositionAndAttach function in ShelfPositionRepository.", e);
        }
    }

    public void deleteShelfPosition(String deviceName, String shelfPositionId) {
        final String cypher = """
                MATCH (d:Device {deviceName: $deviceName})-[p:HAS]->(sp:ShelfPosition {id: $shelfPositionId})
                 OPTIONAL MATCH (sp)-[r:ATTACHED]->(s:Shelf)
                 DELETE p, r
                 REMOVE s:Shelf, sp:ShelfPosition
                 SET s:DeletedShelf, sp:DeletedShelfPosition
                """;

        try {
            var result = driver.executableQuery(cypher).withParameters(Map.of(
                    "deviceName", deviceName,
                    "shelfPositionId", shelfPositionId
            )).execute();

            if (result.summary().counters().relationshipsDeleted() == 0) {
                throw new IllegalArgumentException("Current shelfPosition with id " + shelfPositionId + " is not attached with device deviceName " + deviceName);
            }
        } catch (IllegalArgumentException ie) {
            throw ie;
        } catch (Exception e) {
            System.err.println("Repository Error: Failed to execute deleteShelfPosition method in ShelfPositionRepository. Reason: " + e.getMessage());
            throw new RuntimeException("Failed to execute deleteShelfPosition method in ShelfPositionRepository", e);
        }
    }
}
