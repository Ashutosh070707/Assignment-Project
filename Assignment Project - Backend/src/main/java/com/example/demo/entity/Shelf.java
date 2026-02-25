package com.example.demo.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class Shelf {
    private String id;
    @NotBlank(message = "ShelfName is required")
    private String shelfName;
    @NotBlank(message = "PartNumber is required")
    private String partNumber;

    public Shelf() {
        this.id = UUID.randomUUID().toString();
    }
}
