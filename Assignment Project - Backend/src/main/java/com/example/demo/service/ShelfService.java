package com.example.demo.service;

import com.example.demo.DTO.CreateShelfAndAttach;
import com.example.demo.DTO.DeleteShelf;
import com.example.demo.entity.Shelf;
import com.example.demo.repository.ShelfPositionRepository;
import com.example.demo.repository.ShelfRepository;
import org.springframework.stereotype.Service;

@Service
public class ShelfService {
    private final ShelfRepository shelfRepository;
    private final ShelfPositionRepository shelfPositionRepository;

    public ShelfService(ShelfRepository shelfRepository, ShelfPositionRepository shelfPositionRepository) {
        this.shelfRepository = shelfRepository;
        this.shelfPositionRepository = shelfPositionRepository;
    }

    public Shelf createShelfAndAttach(CreateShelfAndAttach dto) {
        try {
            if (!shelfPositionRepository.isShelfPositionPresent(dto.getShelfPositionId())) {
                throw new RuntimeException("ShelfPosition with id " + dto.getShelfPositionId() + " does not exist in the database");
            }
            if (shelfRepository.isShelfPresent(dto.getShelfName())) {
                throw new RuntimeException("Shelf with name " + dto.getShelfName() + " is already present in the database");
            }
            Shelf shelf = new Shelf();
            shelf.setShelfName(dto.getShelfName());
            shelf.setPartNumber(dto.getPartNumber());
            return shelfRepository.createShelfAndAttach(dto.getShelfPositionId(), shelf).orElseThrow(() -> new RuntimeException("Service Error: Failed to create shelf and attach with the shelfPosition"));
        } catch (Exception e) {
            System.err.println("Service Error: createShelfAndAttach method failed. Reason: " + e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void deleteShelf(DeleteShelf dto) {
        try {
            if (!shelfPositionRepository.isShelfPositionPresent(dto.getShelfPositionId())) {
                throw new RuntimeException("ShelfPosition with id " + dto.getShelfPositionId() + " does not exist in the database");
            }
            if (!shelfRepository.isShelfPresent(dto.getShelfName())) {
                throw new RuntimeException("Shelf with name " + dto.getShelfName() + " does not exist in the database");
            }
            shelfRepository.deleteShelf(dto.getShelfPositionId(), dto.getShelfName());
        } catch (Exception e) {
            System.err.println("Service Error: deleteShelf method failed. Reason: " + e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
