package com.example.demo.controller;

import com.example.demo.DTO.CreateShelfAndAttach;
import com.example.demo.DTO.UpdateShelf;
import com.example.demo.entity.Shelf;
import com.example.demo.service.ShelfService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shelves")
public class ShelfController {
    private final ShelfService shelfService;

    public ShelfController(ShelfService shelfService) {
        this.shelfService = shelfService;
    }

    @PostMapping("/createAndAttach")
    public ResponseEntity<?> createShelfAndAttach(@Valid @RequestBody CreateShelfAndAttach dto) {
        Shelf savedShelf = shelfService.createShelfAndAttach(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedShelf);
    }

    @DeleteMapping("/{shelfName}")
    public ResponseEntity<?> deleteShelf(@PathVariable String shelfName) {
        shelfService.deleteShelf(shelfName);
        return ResponseEntity.status(HttpStatus.OK).body("Shelf deleted successfully");
    }

    @PutMapping("/update")
    public ResponseEntity<?> updateShelf(@Valid @RequestBody UpdateShelf dto) {
        Shelf updatedShelf = shelfService.updateShelf(dto);
        return ResponseEntity.ok(updatedShelf);
    }

    @GetMapping("/check/{newShelfName}")
    public ResponseEntity<?> checkValidity(@PathVariable String newShelfName) {
        shelfService.checkValidity(newShelfName);
        return ResponseEntity.ok("Valid newShelfName");
    }

}
