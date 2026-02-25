package com.example.demo.DTO;

import com.example.demo.entity.Shelf;
import com.example.demo.entity.ShelfPosition;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShelfPair {
    private ShelfPosition shelfPosition;
    private Shelf shelf;
}