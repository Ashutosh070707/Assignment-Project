package com.example.demo.controller;

import com.example.demo.DTO.DeviceSummary;
import com.example.demo.DTO.UpdateDevice;
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
        List<Device> fetchedDevices = deviceService.getAllDevices();
        return ResponseEntity.status(HttpStatus.OK).body(fetchedDevices);
    }

    @GetMapping("/deviceDetails/{deviceName}")
    public ResponseEntity<?> getDeviceDetails(@PathVariable String deviceName) {
        DeviceSummary deviceSummary = deviceService.getDeviceDetails(deviceName);
        return ResponseEntity.ok(deviceSummary);
    }

    @PostMapping("/")
    public ResponseEntity<?> createDevice(@Valid @RequestBody Device device) {
        Device savedDevice = deviceService.createDevice(device);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDevice);
    }

    @DeleteMapping("/{deviceName}")
    public ResponseEntity<?> deleteDevice(@PathVariable String deviceName) {
        deviceService.deleteDevice(deviceName);
        return ResponseEntity.status(HttpStatus.OK).body("Device deleted successfully");
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateDevice(@Valid @RequestBody UpdateDevice dto) {
        Device updatedDevice = deviceService.updateDevice(dto);
        return ResponseEntity.ok(updatedDevice);
    }

    @GetMapping("/check/{newDeviceName}")
    public ResponseEntity<?> checkValidity(@PathVariable String newDeviceName) {
        deviceService.checkValidity(newDeviceName);
        return ResponseEntity.ok("Valid newDeviceName");
    }

}