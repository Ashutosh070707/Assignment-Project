package com.example.demo.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class ShelfPosition {
    private String id;
    @NotBlank(message = "DeviceId is required")
    private String deviceId;

    public ShelfPosition() {
        this.id = UUID.randomUUID().toString();
    }
}
