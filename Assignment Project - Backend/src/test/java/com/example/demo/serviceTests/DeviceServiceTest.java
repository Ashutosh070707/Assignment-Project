package com.example.demo.serviceTests;

import com.example.demo.DTO.DeviceSummary;
import com.example.demo.DTO.UpdateDevice;
import com.example.demo.entity.Device;
import com.example.demo.enums.BuildingName;
import com.example.demo.enums.DeviceType;
import com.example.demo.exception.customExceptions.ResourceAlreadyExists;
import com.example.demo.exception.customExceptions.ResourceNotFound;
import com.example.demo.repository.DeviceRepository;
import com.example.demo.service.DeviceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DeviceServiceTest {
    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeviceService deviceService;

    private Device dummyDevice;

    @BeforeEach
    void setUp() {
        dummyDevice = new Device();
        dummyDevice.setDeviceName("Core-Router-01");
        dummyDevice.setNumberOfShelfPositions(4);
    }

    @Test
    void createDevice_ShouldSucceed_WhenValid() {
        when(deviceRepository.isDevicePresent("Core-Router-01")).thenReturn(false);
        when(deviceRepository.createDevice(dummyDevice)).thenReturn(Optional.of(dummyDevice));

        Device result = deviceService.createDevice(dummyDevice);

        assertNotNull(result);
        assertEquals("Core-Router-01", result.getDeviceName());
        verify(deviceRepository, times(1)).createDevice(dummyDevice);
    }

    @Test
    void createDevice_ShouldThrowResourceAlreadyExists_WhenNameTaken() {
        when(deviceRepository.isDevicePresent("Core-Router-01")).thenReturn(true);

        assertThrows(ResourceAlreadyExists.class, () -> {
            deviceService.createDevice(dummyDevice);
        });
    }

    @Test
    void getDeviceDetails_ShouldReturnSummary_WhenExists() {
        DeviceSummary summary = new DeviceSummary();
        summary.setDevice(dummyDevice);

        when(deviceRepository.isDevicePresent("Core-Router-01")).thenReturn(true);
        when(deviceRepository.getDeviceDetails("Core-Router-01")).thenReturn(Optional.of(summary));

        DeviceSummary result = deviceService.getDeviceDetails("Core-Router-01");

        assertNotNull(result);
        assertEquals("Core-Router-01", result.getDevice().getDeviceName());
    }

    @Test
    void getDeviceDetails_ShouldThrowResourceNotFound_WhenDeviceDoesNotExist() {
        when(deviceRepository.isDevicePresent("Fake-Router")).thenReturn(false);

        assertThrows(ResourceNotFound.class, () -> {
            deviceService.getDeviceDetails("Fake-Router");
        });
    }

    @Test
    void updateDevice_ShouldSucceed_WhenValid() {
        UpdateDevice updateDto = new UpdateDevice("123", "Core-Router-01", "Core-Router-02", "PN", BuildingName.Hanover_US, DeviceType.CORE_ROUTER);

        when(deviceRepository.isDevicePresent("Core-Router-01")).thenReturn(true);
        when(deviceRepository.updateDevice(updateDto)).thenReturn(Optional.of(dummyDevice));

        Device result = deviceService.updateDevice(updateDto);

        assertNotNull(result);
        verify(deviceRepository, times(1)).updateDevice(updateDto);
    }

    @Test
    void deleteDevice_ShouldSucceed_WhenExists() {
        when(deviceRepository.isDevicePresent("Core-Router-01")).thenReturn(true);
        doNothing().when(deviceRepository).deleteDevice("Core-Router-01");

        deviceService.deleteDevice("Core-Router-01");

        verify(deviceRepository, times(1)).deleteDevice("Core-Router-01");
    }
}