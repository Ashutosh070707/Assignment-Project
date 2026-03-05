package com.example.demo.serviceTests;

import com.example.demo.DTO.CreateShelfAndAttach;
import com.example.demo.DTO.UpdateShelf;
import com.example.demo.entity.Shelf;
import com.example.demo.exception.customExceptions.ResourceAlreadyExists;
import com.example.demo.exception.customExceptions.ResourceNotFound;
import com.example.demo.repository.ShelfPositionRepository;
import com.example.demo.repository.ShelfRepository;
import com.example.demo.service.ShelfService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ShelfServiceTest {

    @Mock
    private ShelfRepository shelfRepository;

    @Mock
    private ShelfPositionRepository shelfPositionRepository;

    @InjectMocks
    private ShelfService shelfService;

    private Shelf dummyShelf;
    private CreateShelfAndAttach createDto;

    @BeforeEach
    void setUp() {
        dummyShelf = new Shelf();
        dummyShelf.setShelfName("Firewall-SF-01");

        createDto = new CreateShelfAndAttach();
        createDto.setShelfPositionId("pos-123");
        createDto.setShelfName("Firewall-SF-01");
        createDto.setPartNumber("SH-999");
    }

    @Test
    void createShelfAndAttach_ShouldThrowResourceNotFound_WhenPositionMissing() {
        when(shelfPositionRepository.isShelfPositionPresent("pos-123")).thenReturn(false);

        assertThrows(ResourceNotFound.class, () -> shelfService.createShelfAndAttach(createDto));
        verify(shelfRepository, never()).createShelfAndAttach(anyString(), any(Shelf.class));
    }

    @Test
    void createShelfAndAttach_ShouldThrowResourceAlreadyExists_WhenNameTaken() {
        when(shelfPositionRepository.isShelfPositionPresent("pos-123")).thenReturn(true);
        when(shelfRepository.isShelfPresent("Firewall-SF-01")).thenReturn(true);

        assertThrows(ResourceAlreadyExists.class, () -> shelfService.createShelfAndAttach(createDto));
    }

    @Test
    void createShelfAndAttach_ShouldSucceed_WhenValid() {
        when(shelfPositionRepository.isShelfPositionPresent("pos-123")).thenReturn(true);
        when(shelfRepository.isShelfPresent("Firewall-SF-01")).thenReturn(false);
        // We use any(Shelf.class) because the Service creates a new Shelf object internally
        when(shelfRepository.createShelfAndAttach(eq("pos-123"), any(Shelf.class))).thenReturn(Optional.of(dummyShelf));

        Shelf result = shelfService.createShelfAndAttach(createDto);

        assertNotNull(result);
        assertEquals("Firewall-SF-01", result.getShelfName());
    }

    @Test
    void deleteShelf_ShouldThrowResourceNotFound_WhenDoesNotExist() {
        when(shelfRepository.isShelfPresent("Fake-Shelf")).thenReturn(false);

        assertThrows(ResourceNotFound.class, () -> shelfService.deleteShelf("Fake-Shelf"));
    }

    @Test
    void deleteShelf_ShouldSucceed_WhenExists() {
        when(shelfRepository.isShelfPresent("Firewall-SF-01")).thenReturn(true);
        doNothing().when(shelfRepository).deleteShelf("Firewall-SF-01");

        shelfService.deleteShelf("Firewall-SF-01");

        verify(shelfRepository, times(1)).deleteShelf("Firewall-SF-01");
    }

    @Test
    void updateShelf_ShouldThrowResourceNotFound_WhenOldNameDoesNotExist() {
        UpdateShelf updateDto = new UpdateShelf("123", "Old-Shelf", "New-Shelf", "SH-999");
        when(shelfRepository.isShelfPresent("Old-Shelf")).thenReturn(false);

        assertThrows(ResourceNotFound.class, () -> shelfService.updateShelf(updateDto));
    }

    // --- MISSING UPDATE SCENARIO ---

    @Test
    void updateShelf_ShouldSucceed_WhenValid() {
        UpdateShelf updateDto = new UpdateShelf("123", "Old-Shelf", "New-Shelf", "SH-999");

        // 1. Mock the check to say the old shelf exists
        when(shelfRepository.isShelfPresent("Old-Shelf")).thenReturn(true);

        // 2. Mock the actual update
        when(shelfRepository.updateShelf(updateDto)).thenReturn(Optional.of(dummyShelf));

        Shelf result = shelfService.updateShelf(updateDto);

        assertNotNull(result);
        verify(shelfRepository, times(1)).updateShelf(updateDto);
    }

    // --- MISSING CHECK VALIDITY SCENARIOS ---

    @Test
    void checkValidity_ShouldThrowResourceAlreadyExists_WhenNameTaken() {
        // If the repository says the name is found...
        when(shelfRepository.isShelfPresent("Taken-Shelf")).thenReturn(true);

        // ...it should throw the custom exception
        assertThrows(ResourceAlreadyExists.class, () -> shelfService.checkValidity("Taken-Shelf"));
    }

    @Test
    void checkValidity_ShouldSucceed_WhenNameIsAvailable() {
        // If the repository says the name is NOT found...
        when(shelfRepository.isShelfPresent("Available-Shelf")).thenReturn(false);

        // ...it should simply run without throwing any errors
        assertDoesNotThrow(() -> shelfService.checkValidity("Available-Shelf"));
    }
}