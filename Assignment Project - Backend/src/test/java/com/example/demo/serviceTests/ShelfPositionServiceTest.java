package com.example.demo.serviceTests;

import com.example.demo.entity.ShelfPosition;
import com.example.demo.exception.customExceptions.DatabaseOperationException;
import com.example.demo.exception.customExceptions.ResourceNotFound;
import com.example.demo.repository.DeviceRepository;
import com.example.demo.repository.ShelfPositionRepository;
import com.example.demo.service.ShelfPositionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShelfPositionServiceTest {
    @Mock
    private ShelfPositionRepository shelfPositionRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private ShelfPositionService shelfPositionService;

    private ShelfPosition dummyPosition;

    @BeforeEach
    void setUp() {
        dummyPosition = new ShelfPosition();
        dummyPosition.setId("pos-123");
        dummyPosition.setDeviceId("device-123");
    }

    @Test
    void createShelfPositionAndAttach_ShouldSucceed_WhenValid() {
        when(shelfPositionRepository.createShelfPositionAndAttach(any(ShelfPosition.class))).thenReturn(Optional.of(dummyPosition));

        ShelfPosition result = shelfPositionService.createShelfPositionAndAttach(dummyPosition);

        assertNotNull(result);
        assertEquals("pos-123", result.getId());
        verify(shelfPositionRepository, times(1)).createShelfPositionAndAttach(dummyPosition);
    }

    @Test
    void createShelfPositionAndAttach_ShouldThrowException_WhenCreationFails() {
        when(shelfPositionRepository.createShelfPositionAndAttach(any(ShelfPosition.class))).thenReturn(Optional.empty());

        assertThrows(DatabaseOperationException.class, () -> shelfPositionService.createShelfPositionAndAttach(dummyPosition));
    }

    @Test
    void deleteShelfPosition_ShouldThrowResourceNotFound_WhenDeviceMissing() {
        when(deviceRepository.isDevicePresent("Fake-Router")).thenReturn(false);

        assertThrows(ResourceNotFound.class, () -> shelfPositionService.deleteShelfPosition("Fake-Router", "pos-123"));
        verify(shelfPositionRepository, never()).deleteShelfPosition(anyString(), anyString());
    }

    @Test
    void deleteShelfPosition_ShouldThrowResourceNotFound_WhenPositionMissing() {
        when(deviceRepository.isDevicePresent("Core-Router-01")).thenReturn(true);
        when(shelfPositionRepository.isShelfPositionPresent("fake-pos")).thenReturn(false);

        assertThrows(ResourceNotFound.class, () -> shelfPositionService.deleteShelfPosition("Core-Router-01", "fake-pos"));
        verify(shelfPositionRepository, never()).deleteShelfPosition(anyString(), anyString());
    }

    @Test
    void deleteShelfPosition_ShouldSucceed_WhenBothExist() {
        when(deviceRepository.isDevicePresent("Core-Router-01")).thenReturn(true);
        when(shelfPositionRepository.isShelfPositionPresent("pos-123")).thenReturn(true);
        doNothing().when(shelfPositionRepository).deleteShelfPosition("Core-Router-01", "pos-123");

        shelfPositionService.deleteShelfPosition("Core-Router-01", "pos-123");

        verify(shelfPositionRepository, times(1)).deleteShelfPosition("Core-Router-01", "pos-123");
    }
}