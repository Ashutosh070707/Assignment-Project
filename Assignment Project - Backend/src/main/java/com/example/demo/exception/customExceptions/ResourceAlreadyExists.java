package com.example.demo.exception.customExceptions;

public class ResourceAlreadyExists extends RuntimeException {
    public ResourceAlreadyExists(String message) {
        super(message);
    }

    public ResourceAlreadyExists(String resourceName, String field, String value) {
        super(String.format("%s with %s - %s already exists.", resourceName, field, value));
    }
}
