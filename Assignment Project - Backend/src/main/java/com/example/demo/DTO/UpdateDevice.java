package com.example.demo.DTO;

import com.example.demo.enums.BuildingName;
import com.example.demo.enums.DeviceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateDevice {
    private String id;
    @NotBlank(message = "OldDeviceName is required")
    private String oldDeviceName;
    @NotBlank(message = "NewDeviceName is required")
    private String newDeviceName;
    @NotBlank(message = "NewPartNumber is required")
    private String newPartNumber;
    @NotNull(message = "NewBuildingName is required")
    private BuildingName newBuildingName;
    @NotNull(message = "NewDeviceType is required")
    private DeviceType newDeviceType;
}
