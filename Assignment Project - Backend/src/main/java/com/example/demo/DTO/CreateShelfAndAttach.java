package com.example.demo.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateShelfAndAttach {
    @NotBlank(message = "ShelfPosition can not be empty")
    private String shelfPositionId;
    @NotBlank(message = "ShelfName is required")
    private String shelfName;
    @NotBlank(message = "PartNumber is required")
    private String partNumber;
}
