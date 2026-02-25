package com.example.demo.service;

import com.example.demo.DTO.DeviceSummary;
import com.example.demo.entity.Device;
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
        try {
            return deviceRepository.getAllDevices();
        } catch (Exception e) {
            System.err.println("Service Error: getAllDevices function failed. Reason: " + e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public DeviceSummary getDeviceDetails(String deviceName) {
        try {
            if (!deviceRepository.isDevicePresent(deviceName)) {
                throw new IllegalArgumentException("Device with name : " + deviceName + " does not exists in the database");
            }
            return deviceRepository.getDeviceDetails(deviceName).orElseThrow(() -> new RuntimeException("Service Error: Failed to fetch device details"));
        } catch (IllegalArgumentException ie) {
            System.err.println(ie.getMessage());
            throw (ie);
        } catch (Exception e) {
            System.err.println("Service Error: getDeviceDetails function failed. Reason: " + e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Device createDevice(Device device) {
        try {
            if (deviceRepository.isDevicePresent(device.getDeviceName())) {
                throw new RuntimeException("Device with this name is already present");
            }
            return deviceRepository.createDevice(device).orElseThrow(() -> new RuntimeException("Service Error: Failed to create device in the database"));
        } catch (Exception e) {
            System.err.println("Service Error: createDevice function failed. Reason: " + e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void deleteDevice(String deviceName) {
        try {
            if (!deviceRepository.isDevicePresent(deviceName)) {
                throw new RuntimeException("Device with name: " + deviceName + " does not exist in database");
            }
            deviceRepository.deleteDevice(deviceName);
        } catch (Exception e) {
            System.err.println("Service Error: deleteDevice function failed. Reason: " + e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
