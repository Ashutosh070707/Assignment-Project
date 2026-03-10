package com.example.demo.repository;

import com.example.demo.DTO.DeviceSummary;
import com.example.demo.DTO.ShelfPair;
import com.example.demo.DTO.UpdateDevice;
import com.example.demo.entity.Device;
import com.example.demo.entity.Shelf;
import com.example.demo.entity.ShelfPosition;
import com.example.demo.enums.BuildingName;
import com.example.demo.enums.DeviceType;
import com.example.demo.exception.customExceptions.DatabaseOperationException;
import org.neo4j.driver.Driver;
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
            System.err.println("Repository Error: Failed to execute isDevicePresent method. Reason: " + e.getMessage());
            throw new DatabaseOperationException("Database error occurred while checking whether device is present in database or not.");
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
            System.err.println("Repository Error: Failed to execute getAllDevices method. Reason: " + e.getMessage());
            throw new DatabaseOperationException("Database error occurred while fetching all devices.");
        }
    }

    public Optional<DeviceSummary> getDeviceDetails(String deviceName) {
        final String cypher = """
                MATCH (d:Device {deviceName: $deviceName})
                OPTIONAL MATCH (d)-[:HAS]->(sp:ShelfPosition)
                OPTIONAL MATCH (sp)-[:ATTACHED]->(s:Shelf)
                RETURN d as device, collect({shelfPosition: sp, shelf: s}) as shelfPairs;
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

            List<ShelfPair> shelfPairs = new ArrayList<>();
            record.get("shelfPairs").asList(value -> value).forEach(pairValue -> {
                org.neo4j.driver.Value spValue = pairValue.get("shelfPosition");
                org.neo4j.driver.Value sValue = pairValue.get("shelf");

                if (!spValue.isNull()) {
                    org.neo4j.driver.types.Node spNode = spValue.asNode();
                    ShelfPosition shelfPosition = new ShelfPosition();
                    shelfPosition.setId(spNode.get("id").asString());
                    shelfPosition.setDeviceId(spNode.get("deviceId").asString());

                    Shelf shelf = null;

                    if (!sValue.isNull()) {
                        org.neo4j.driver.types.Node sNode = sValue.asNode();
                        shelf = new Shelf();
                        shelf.setId(sNode.get("id").asString());
                        shelf.setShelfName(sNode.get("shelfName").asString());
                        shelf.setPartNumber(sNode.get("partNumber").asString());
                    }

                    shelfPairs.add(new ShelfPair(shelfPosition, shelf));
                }
            });
            deviceSummary.setShelfPairs(shelfPairs);

            return Optional.of(deviceSummary);
        } catch (Exception e) {
            System.err.println("Repository Error: Failed to execute getDeviceDetails method. Reason: " + e.getMessage());
            throw new DatabaseOperationException("Database error occurred while fetching device details.");
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
            System.err.println("Repository Error: Failed to execute createDevice method. Reason: " + e.getMessage());
            throw new DatabaseOperationException("Database error occurred while creating device.");
        }
    }

    public void deleteDevice(String deviceName) {
        final String cypher = """
                                MATCH (d:Device {deviceName: $deviceName})
                                OPTIONAL MATCH (d)-[r:HAS]->(sp:ShelfPosition)
                                OPTIONAL MATCH (sp)-[p:ATTACHED]->(s:Shelf)
                                DELETE p
                                REMOVE d:Device, sp:ShelfPosition, s:Shelf
                                SET d:DeletedDevice, sp:DeletedShelfPosition, s:DeletedShelf
                """;

        try {
            var result = driver.executableQuery(cypher).withParameters(Map.of(
                    "deviceName", deviceName
            )).execute();
        } catch (Exception e) {
            System.err.println("Repository Error: Failed to execute deleteDevice method. Reason: " + e.getMessage());
            throw new DatabaseOperationException("Database error occurred while deleting device.");
        }
    }

    public Optional<Device> updateDevice(UpdateDevice dto) {
        final String cypher = """
                MATCH (d:Device {deviceName: $oldDeviceName})
                SET d.deviceName = $newDeviceName,
                d.partNumber = $newPartNumber,
                d.deviceType = $newDeviceType,
                d.buildingName = $newBuildingName
                RETURN d;
                """;

        try {
            var result = driver.executableQuery(cypher).withParameters(Map.of(
                    "oldDeviceName", dto.getOldDeviceName(),
                    "newDeviceName", dto.getNewDeviceName(),
                    "newPartNumber", dto.getNewPartNumber(),
                    "newDeviceType", dto.getNewDeviceType().name(),
                    "newBuildingName", dto.getNewBuildingName().name()
            )).execute();

            if (result.records().isEmpty()) {
                return Optional.empty();
            }

            var record = result.records().getFirst();
            Node node = record.get("d").asNode();
            Device updatedDevice = new Device();
            updatedDevice.setId(node.get("id").asString());
            updatedDevice.setDeviceName(node.get("deviceName").asString());
            updatedDevice.setPartNumber(node.get("partNumber").asString());
            updatedDevice.setBuildingName(BuildingName.valueOf(node.get("buildingName").asString()));
            updatedDevice.setDeviceType(DeviceType.valueOf(node.get("deviceType").asString()));
            updatedDevice.setNumberOfShelfPositions(node.get("numberOfShelfPositions").asInt());

            return Optional.of(updatedDevice);
        } catch (Exception e) {
            System.err.println("Repository Error: Failed to execute updateDevice method. Reason: " + e.getMessage());
            throw new DatabaseOperationException("Database error occurred while updating device.");
        }
    }
}
