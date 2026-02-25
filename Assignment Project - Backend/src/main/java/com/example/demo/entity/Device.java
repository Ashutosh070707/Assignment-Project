package com.example.demo.entity;

import com.example.demo.enums.BuildingName;
import com.example.demo.enums.DeviceType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class Device {
    private String id;
    @NotBlank(message = "DeviceName is required")
    private String deviceName;
    @NotBlank(message = "PartNumber is required")
    private String partNumber;
    @NotNull(message = "BuildingName is required")
    private BuildingName buildingName;
    @NotNull(message = "DeviceType is required")
    private DeviceType deviceType;
    @NotNull(message = "Number of shelfPositions is required")
    @Min(value = 1, message = "The number of shelf positions cannot be less than 0")
    @Max(value = 14, message = "The number of shelf positions cannot exceed 14")
    private Integer numberOfShelfPositions;

    public Device() {
        this.id = UUID.randomUUID().toString();
    }
}
