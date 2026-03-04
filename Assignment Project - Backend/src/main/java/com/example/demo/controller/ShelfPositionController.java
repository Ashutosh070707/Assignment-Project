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
        ShelfPosition savedShelfPosition = shelfPositionService.createShelfPositionAndAttach(shelfPosition);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedShelfPosition);
    }

    @DeleteMapping("/{deviceName}/{shelfPositionId}")
    public ResponseEntity<?> deleteShelfPosition(@PathVariable String deviceName, @PathVariable String shelfPositionId) {
        shelfPositionService.deleteShelfPosition(deviceName, shelfPositionId);
        return ResponseEntity.status(HttpStatus.OK).body("ShelfPosition deleted successfully");
    }
}
