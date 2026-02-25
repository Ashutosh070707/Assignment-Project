package com.example.demo.controller;

import com.example.demo.DTO.DeviceSummary;
import com.example.demo.entity.Device;
import com.example.demo.service.DeviceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {
    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping("/allDevices")
    public ResponseEntity<?> getAllDevices() {
        try {
            List<Device> fetchedDevices = deviceService.getAllDevices();
            return ResponseEntity.status(HttpStatus.OK).body(fetchedDevices);
        } catch (Exception e) {
            System.err.println("Controller Error: Error in getAllDevices in DeviceController. Reason: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/deviceDetails/{deviceName}")
    public ResponseEntity<?> getDeviceDetails(@PathVariable String deviceName) {
        try {
            DeviceSummary deviceSummary = deviceService.getDeviceDetails(deviceName);
            return ResponseEntity.status(HttpStatus.OK).body(deviceSummary);
        } catch (Exception e) {
            System.err.println("Controller Error: Error in getDeviceDetails in DeviceController. Reason: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/")
    public ResponseEntity<?> createDevice(@Valid @RequestBody Device device) {
        try {
            Device savedDevice = deviceService.createDevice(device);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedDevice);
        } catch (Exception e) {
            System.err.println("Controller Error: Error in createDevice in DeviceController. Reason: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{deviceName}")
    public ResponseEntity<?> deleteDevice(@PathVariable String deviceName) {
        try {
            deviceService.deleteDevice(deviceName);
            return ResponseEntity.status(HttpStatus.OK).body("Device deleted successfully");
        } catch (Exception e) {
            System.err.println("Controller Error: Error in deleteDevice in DeviceController. Reason: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}