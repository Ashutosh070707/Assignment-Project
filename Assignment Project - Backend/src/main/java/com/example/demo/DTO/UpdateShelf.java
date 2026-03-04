package com.example.demo.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateShelf {
    private String id;
    @NotBlank(message = "Previous ShelfName is required")
    private String previousShelfName;
    @NotBlank(message = "ShelfName is required")
    private String newShelfName;
    @NotBlank(message = "PartNumber is required")
    private String newPartNumber;
}
