package com.example.demo.repository;

import com.example.demo.DTO.DeviceSummary;
import com.example.demo.entity.Device;
import com.example.demo.entity.Shelf;
import com.example.demo.entity.ShelfPosition;
import com.example.demo.enums.BuildingName;
import com.example.demo.enums.DeviceType;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Node;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class DeviceRepository {

    private final Driver driver;

    public DeviceRepository(Driver driver) {
        this.driver = driver;
    }

    public boolean isDevicePresent(String deviceName) {
        final String cypher = """
                MATCH (d:Device {deviceName: $deviceName}) return d;
                """;
        try {
            var result = driver.executableQuery(cypher).withParameters(Map.of(
                    "deviceName", deviceName
            )).execute();

            return !result.records().isEmpty();
        } catch (Exception e) {
            System.err.println("Repository Error: Failed to check if device exists. Reason: " + e.getMessage());
            throw new RuntimeException("Database error while checking device existence.", e);
        }
    }

    public List<Device> getAllDevices() {
        final String cypher = """
                MATCH (d:Device) RETURN d;
                """;

        try {
            var result = driver.executableQuery(cypher).execute();

            List<Device> allDevices = new ArrayList<>();

            result.records().forEach((record) -> {
                Node node = record.get("d").asNode();
                Device device = new Device();
                device.setId(node.get("id").asString());
                device.setDeviceName(node.get("deviceName").asString());
                device.setPartNumber(node.get("partNumber").asString());
                device.setNumberOfShelfPositions(node.get("numberOfShelfPositions").asInt());
                device.setDeviceType(DeviceType.valueOf(node.get("deviceType").asString()));
                device.setBuildingName(BuildingName.valueOf(node.get("buildingName").asString()));

                allDevices.add(device);
            });

            return allDevices;
        } catch (Exception e) {
            System.err.println("Repository Error: Failed to execute getAllDevices method in DeviceRepository" + e.getMessage());
            throw new RuntimeException("Failed to execute getAllDevices method in DeviceRepository", e);
        }
    }

    public Optional<DeviceSummary> getDeviceDetails(String deviceName) {
        final String cypher = """
                MATCH (d:Device {deviceName: $deviceName})
                OPTIONAL MATCH (d)-[r:HAS]->(sp:ShelfPosition)
                OPTIONAL MATCH (sp)-[p:ATTACHED]->(s:Shelf)
                RETURN d as device, collect(sp) as shelfPositions, collect(s) as shelves
                """;

        try {
            var result = driver.executableQuery(cypher).withParameters(Map.of(
                    "deviceName", deviceName
            )).execute();

            if (result.records().isEmpty()) return Optional.empty();
            var record = result.records().getFirst();

            DeviceSummary deviceSummary = new DeviceSummary();

            Node deviceNode = record.get("device").asNode();
            Device device = new Device();
            device.setId(deviceNode.get("id").asString());
            device.setDeviceName(deviceNode.get("deviceName").asString());
            device.setPartNumber(deviceNode.get("partNumber").asString());
            device.setNumberOfShelfPositions(deviceNode.get("numberOfShelfPositions").asInt());
            device.setBuildingName(BuildingName.valueOf(deviceNode.get("buildingName").asString()));
            device.setDeviceType(DeviceType.valueOf(deviceNode.get("deviceType").asString()));

            deviceSummary.setDevice(device);

            List<ShelfPosition> shelfPositions = new ArrayList<>();
            record.get("shelfPositions").asList(value -> value.asNode()).forEach(spNode -> {
                ShelfPosition shelfPosition = new ShelfPosition();
                shelfPosition.setId(spNode.get("id").asString());
                shelfPosition.setDeviceId(spNode.get("deviceId").asString());
                shelfPositions.add(shelfPosition);
            });

            deviceSummary.setShelfPositions(shelfPositions);

            List<Shelf> shelves = new ArrayList<>();

            record.get("shelves").asList(Value::asNode).forEach(sNode -> {
                Shelf shelf = new Shelf();
                shelf.setId(sNode.get("id").asString());
                shelf.setShelfName(sNode.get("shelfName").asString());
                shelf.setPartNumber(sNode.get("partNumber").asString());
                shelves.add(shelf);
            });

            deviceSummary.setShelves(shelves);

            return Optional.of(deviceSummary);
        } catch (Exception e) {
            System.err.println("Repository Error: Failed to execute getDeviceDetails function in DeviceRepository" + e.getMessage());
            throw new RuntimeException("Failed to execute getDeviceDetails function in DeviceRepository", e);
        }
    }

    public Optional<Device> createDevice(Device device) {
        final String cypher = """
                MERGE (d:Device { id: $id})
                ON CREATE SET
                    d.deviceName= $deviceName,
                    d.partNumber= $partNumber,
                    d.buildingName= $buildingName,
                    d.deviceType= $deviceType,
                    d.numberOfShelfPositions= $numberOfShelfPositions
                WITH d
                UNWIND range(1, $numberOfShelfPositions) as i
                CREATE (sp:ShelfPosition {
                id: randomUUID(),
                deviceId: d.id
                })
                CREATE (d)-[r:HAS]->(sp)
                RETURN DISTINCT d, collect(sp) as shelfPositions
                """;


        try {
            var result = driver.executableQuery(cypher).withParameters(Map.of(
                            "id", device.getId(),
                            "deviceName", device.getDeviceName(),
                            "partNumber", device.getPartNumber(),
                            "buildingName", device.getBuildingName().name(),
                            "deviceType", device.getDeviceType().name(),
                            "numberOfShelfPositions", device.getNumberOfShelfPositions()
                    ))
                    .execute();

            if (result.records().isEmpty()) {
                return Optional.empty();
            }

            var record = result.records().getFirst();
            Node node = record.get("d").asNode();
            Device savedDevice = new Device();
            savedDevice.setId(node.get("id").asString());
            savedDevice.setDeviceName(node.get("deviceName").asString());
            savedDevice.setPartNumber(node.get("partNumber").asString());
            savedDevice.setBuildingName(BuildingName.valueOf(node.get("buildingName").asString()));
            savedDevice.setDeviceType(DeviceType.valueOf(node.get("deviceType").asString()));
            savedDevice.setNumberOfShelfPositions(node.get("numberOfShelfPositions").asInt());

            return Optional.of(savedDevice);
        } catch (Exception e) {
            System.err.println("Repository Error: Failed to execute Cypher in createDevice function in DeviceRepository" + e.getMessage());
            throw new RuntimeException("Failed to execute Cypher in createDevice function in DeviceRepository.", e);
        }
    }

    public void deleteDevice(String deviceName) {
        final String cypher = """
                MATCH (d:Device {deviceName: $deviceName})
                OPTIONAL MATCH (d)-[r:HAS]->(sp:ShelfPosition)
                OPTIONAL MATCH (sp)-[p:ATTACHED]->(s:Shelf)
                DELETE r, p
                REMOVE d:Device, sp:ShelfPosition, s:Shelf
                SET d:DeletedDevice, sp:DeletedShelfPosition, s:DeletedShelf
                """;

        try {
            var result = driver.executableQuery(cypher).withParameters(Map.of(
                    "deviceName", deviceName
            )).execute();

            if (result.summary().counters().labelsRemoved() == 0) {
                throw new IllegalArgumentException("Device with name '" + deviceName + "' does not exist or is already deleted.");
            }
        } catch (IllegalArgumentException ie) {
            throw ie;
        } catch (Exception e) {
            System.err.println("Repository Error: Failed to execute deleteDevice in DeviceRepository. Reason: " + e.getMessage());
            throw new RuntimeException("Failed to execute deleteDevice in DeviceRepository", e);
        }
    }
}
