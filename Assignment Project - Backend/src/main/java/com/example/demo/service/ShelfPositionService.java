package com.example.demo.service;

import com.example.demo.entity.ShelfPosition;
import com.example.demo.exception.customExceptions.DatabaseOperationException;
import com.example.demo.exception.customExceptions.InvalidArgumentException;
import com.example.demo.exception.customExceptions.ResourceNotFound;
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
        if (!shelfPositionRepository.canAddNewShelfPosition(shelfPosition.getDeviceId())) {
            throw new InvalidArgumentException("Device cannot have more than 14 shelfPositions");
        }
        return shelfPositionRepository.createShelfPositionAndAttach(shelfPosition).orElseThrow(() -> new DatabaseOperationException("create", "shelfPosition"));
    }

    public void deleteShelfPosition(String deviceName, String shelfPositionId) {
        if (!deviceRepository.isDevicePresent(deviceName)) {
            throw new ResourceNotFound("Device", "name", deviceName);
        }
        if (!shelfPositionRepository.isShelfPositionPresent(shelfPositionId)) {
            throw new ResourceNotFound("ShelfPosition", "id", shelfPositionId);
        }
        shelfPositionRepository.deleteShelfPosition(deviceName, shelfPositionId);
    }
}
