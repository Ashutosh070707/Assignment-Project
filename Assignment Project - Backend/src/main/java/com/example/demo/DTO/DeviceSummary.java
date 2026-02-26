package com.example.demo.DTO;

import com.example.demo.entity.Device;

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
    private List<ShelfPair> shelfPairs = new ArrayList<>();
}
 