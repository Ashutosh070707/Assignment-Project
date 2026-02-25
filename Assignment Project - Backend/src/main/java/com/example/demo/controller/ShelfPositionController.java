package com.example.demo.controller;

import com.example.demo.entity.ShelfPosition;
import com.example.demo.service.ShelfPositionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shelfPositions")
public class ShelfPositionController {
    private final ShelfPositionService shelfPositionService;

    public ShelfPositionController(ShelfPositionService shelfPositionService) {
        this.shelfPositionService = shelfPositionService;
    }

    @PostMapping("/")
    public ResponseEntity<?> createShelfPositionAndAttach(@Valid @RequestBody ShelfPosition shelfPosition) {
        try {
            ShelfPosition savedShelfPosition = shelfPositionService.createShelfPositionAndAttach(shelfPosition);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedShelfPosition);
        } catch (Exception e) {
            System.err.println("Controller Error: Error in createShelfPositionAndAttach in ShelfPositionController. Reason: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{deviceName}/{shelfPositionId}")
    public ResponseEntity<?> deleteShelfPosition(@PathVariable String deviceName, @PathVariable String shelfPositionId) {
        try {
            shelfPositionService.deleteShelfPosition(deviceName, shelfPositionId);
            return ResponseEntity.status(HttpStatus.OK).body("ShelfPosition deleted successfully");
        } catch (Exception e) {
            System.err.println("Controller Error: Error in deleteShelfPosition in ShelfPositionController. Reason: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
