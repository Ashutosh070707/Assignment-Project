package com.example.demo.service;

import com.example.demo.entity.ShelfPosition;
import com.example.demo.repository.DeviceRepository;
import com.example.demo.repository.ShelfPositionRepository;
import org.springframework.stereotype.Service;

@Service
public class ShelfPositionService {
    private final ShelfPositionRepository shelfPositionRepository;
    private final DeviceRepository deviceRepository;

    public ShelfPositionService(ShelfPositionRepository shelfPositionRepository, DeviceRepository deviceRepository) {
        this.shelfPositionRepository = shelfPositionRepository;
        this.deviceRepository = deviceRepository;
    }

    public ShelfPosition createShelfPositionAndAttach(ShelfPosition shelfPosition) {
        try {
            return shelfPositionRepository.createShelfPositionAndAttach(shelfPosition).orElseThrow(() -> new RuntimeException("Service Error: Failed to create shelf and attach in the database"));

        } catch (Exception e) {
            System.err.println("Service Error: createShelfPositionAndAttach function failed. Reason: " + e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void deleteShelfPosition(String deviceName, String shelfPositionId) {
        try {
            if (!deviceRepository.isDevicePresent(deviceName)) {
                throw new RuntimeException("Device with name: " + deviceName + " does not exist in the database");
            }
            if (!shelfPositionRepository.isShelfPositionPresent(shelfPositionId)) {
                throw new RuntimeException("ShelfPosition with id " + shelfPositionId + " does not exist in the database");
            }
            shelfPositionRepository.deleteShelfPosition(deviceName, shelfPositionId);
        } catch (Exception e) {
            System.err.println("Service Error: deleteShelfPosition function failed. Reason: " + e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
