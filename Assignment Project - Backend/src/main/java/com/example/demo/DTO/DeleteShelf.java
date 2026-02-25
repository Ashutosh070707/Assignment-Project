package com.example.demo.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeleteShelf {
    @NotBlank(message = "ShelfPositionId cannot be empty")
    private String shelfPositionId;
    @NotBlank(message = "ShelfId cannot be empty")
    private String shelfName;
}
