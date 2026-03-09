package com.example.demo.repository;

import com.example.demo.entity.ShelfPosition;
import com.example.demo.exception.customExceptions.DatabaseOperationException;
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
            System.err.println("Repository Error: Failed to execute isShelfPositionPresent method. Reason: " + e.getMessage());
            throw new DatabaseOperationException("Database error occurred while checking whether shelfPosition is present in database or not.");
        }
    }

    public boolean canAddNewShelfPosition(String deviceId) {
        final String cypher = """
                MATCH (d:Device {id: $deviceId})
                RETURN CASE
                       WHEN coalesce(d.numberOfShelfPositions, 0) >= 14 THEN false
                       ELSE true
                    END AS canAddShelfPosition;
                """;

        try {
            var result = driver.executableQuery(cypher).withParameters(Map.of(
                    "deviceId", deviceId
            )).execute();

            if (result.records().isEmpty()) return false;

            var record = result.records().getFirst();

            return record.get("canAddShelfPosition").asBoolean();
        } catch (Exception e) {
            System.err.println("Repository Error: Failed to execute canAddNewShelfPosition method. Reason: " + e.getMessage());
            throw new DatabaseOperationException("Database error occurred while checking whether we can add new shelfPosition or not.");
        }
    }

    public Optional<ShelfPosition> createShelfPositionAndAttach(ShelfPosition shelfPosition) {
        final String cypher = """
                MATCH (d:Device {
                id: $deviceId
                })
                SET d.numberOfShelfPositions = coalesce(d.numberOfShelfPositions, 0) + 1
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
            System.err.println("Repository Error: Failed to execute createShelfPositionAndAttach method. Reason: " + e.getMessage());
            throw new DatabaseOperationException("Database error occurred while creating shelfPosition.");
        }
    }

    public void deleteShelfPosition(String deviceName, String shelfPositionId) {
        final String cypher = """
                 MATCH (d:Device {deviceName: $deviceName})-[p:HAS]->(sp:ShelfPosition {id: $shelfPositionId})
                 OPTIONAL MATCH (sp)-[r:ATTACHED]->(s:Shelf)
                 SET d.numberOfShelfPositions = coalesce(d.numberOfShelfPositions, 0) - 1
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

        } catch (Exception e) {
            System.err.println("Repository Error: Failed to execute deleteShelfPosition method. Reason: " + e.getMessage());
            throw new DatabaseOperationException("Database error occurred while deleting shelfPosition.");
        }
    }
}
