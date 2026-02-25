package com.example.demo.DTO;

import com.example.demo.entity.Device;
import com.example.demo.entity.Shelf;
import com.example.demo.entity.ShelfPosition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceSummary {
    private Device device;
    private List<ShelfPosition> shelfPositions = new ArrayList<>();
    private List<Shelf> shelves = new ArrayList<>();
}
