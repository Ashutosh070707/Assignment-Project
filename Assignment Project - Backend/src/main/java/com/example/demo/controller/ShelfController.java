package com.example.demo.controller;

import com.example.demo.DTO.CreateShelfAndAttach;
import com.example.demo.DTO.DeleteShelf;
import com.example.demo.entity.Shelf;
import com.example.demo.service.ShelfService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shelfs")
public class ShelfController {
    private final ShelfService shelfService;

    public ShelfController(ShelfService shelfService) {
        this.shelfService = shelfService;
    }


    @PostMapping("/createAndAttach")
    public ResponseEntity<?> createShelfAndAttach(@Valid @RequestBody CreateShelfAndAttach dto) {
        try {
            Shelf savedShelf = shelfService.createShelfAndAttach(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedShelf);
        } catch (Exception e) {
            System.err.println("Controller Error: Error in createShelfAndAttach in ShelfController. Reason: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteShelf(@Valid @RequestBody DeleteShelf dto) {
        try {
            shelfService.deleteShelf(dto);
            return ResponseEntity.status(HttpStatus.OK).body("Shelf deleted successfully");
        } catch (Exception e) {
            System.err.println("Controller Error: Error in deleteShelf in ShelfController. Reason: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
