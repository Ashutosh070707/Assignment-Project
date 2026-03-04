package com.example.demo.service;

import com.example.demo.DTO.DeviceSummary;
import com.example.demo.DTO.UpdateDevice;
import com.example.demo.entity.Device;
import com.example.demo.exception.customExceptions.DatabaseOperationException;
import com.example.demo.exception.customExceptions.ResourceAlreadyExists;
import com.example.demo.exception.customExceptions.ResourceNotFound;
import com.example.demo.repository.DeviceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeviceService {
    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public List<Device> getAllDevices() {
        return deviceRepository.getAllDevices();
    }

    public DeviceSummary getDeviceDetails(String deviceName) {
        if (!deviceRepository.isDevicePresent(deviceName)) {
            throw new ResourceNotFound("Device", "name", deviceName);
        }
        return deviceRepository.getDeviceDetails(deviceName).orElseThrow(() -> new DatabaseOperationException("Failed to fetch device details"));
    }

    public Device createDevice(Device device) {
        if (deviceRepository.isDevicePresent(device.getDeviceName())) {
            throw new ResourceAlreadyExists("Device", "name", device.getDeviceName());
        }
        return deviceRepository.createDevice(device).orElseThrow(() -> new DatabaseOperationException("create", "device"));
    }

    public void deleteDevice(String deviceName) {
        if (!deviceRepository.isDevicePresent(deviceName)) {
            throw new ResourceNotFound("Device", "name", deviceName);
        }
        deviceRepository.deleteDevice(deviceName);
    }

    public Device updateDevice(UpdateDevice dto) {
        if (!deviceRepository.isDevicePresent(dto.getOldDeviceName())) {
            throw new ResourceNotFound("Device", "name", dto.getOldDeviceName());
        }
        return deviceRepository.updateDevice(dto).orElseThrow(() -> new DatabaseOperationException("update", "device"));
    }

    public void checkValidity(String newDeviceName) {
        if (deviceRepository.isDevicePresent(newDeviceName)) {
            throw new ResourceAlreadyExists("Device", "name", newDeviceName);
        }
    }
}
