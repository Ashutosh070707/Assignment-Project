package com.example.demo.service;

import com.example.demo.DTO.CreateShelfAndAttach;
import com.example.demo.DTO.DeleteShelf;
import com.example.demo.DTO.UpdateShelf;
import com.example.demo.entity.Shelf;
import com.example.demo.exception.customExceptions.DatabaseOperationException;
import com.example.demo.exception.customExceptions.ResourceAlreadyExists;
import com.example.demo.exception.customExceptions.ResourceNotFound;
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
        if (!shelfPositionRepository.isShelfPositionPresent(dto.getShelfPositionId())) {
            throw new ResourceNotFound("ShelfPosition", "id", dto.getShelfPositionId());
        }
        if (shelfRepository.isShelfPresent(dto.getShelfName())) {
            throw new ResourceAlreadyExists("Shelf", "name", dto.getShelfName());
        }
        Shelf shelf = new Shelf();
        shelf.setShelfName(dto.getShelfName());
        shelf.setPartNumber(dto.getPartNumber());
        return shelfRepository.createShelfAndAttach(dto.getShelfPositionId(), shelf).orElseThrow(() -> new DatabaseOperationException("create", "shelf"));
    }

    public void deleteShelf(String shelfName) {
        if (!shelfRepository.isShelfPresent(shelfName)) {
            throw new ResourceNotFound("Shelf", "name", shelfName);
        }
        shelfRepository.deleteShelf(shelfName);
    }

    public Shelf updateShelf(UpdateShelf dto) {
        if (!shelfRepository.isShelfPresent(dto.getPreviousShelfName())) {
            throw new ResourceNotFound("Shelf", "name", dto.getPreviousShelfName());
        }
        return shelfRepository.updateShelf(dto).orElseThrow(() -> new DatabaseOperationException("update", "shelf"));
    }

    public void checkValidity(String newShelfName) {
        if (shelfRepository.isShelfPresent(newShelfName)) {
            throw new ResourceAlreadyExists("Shelf", "name", newShelfName);
        }
    }
}
