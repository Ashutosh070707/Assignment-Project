package com.example.demo.exception.customExceptions;

public class ResourceNotFound extends RuntimeException {
    public ResourceNotFound(String message) {
        super(message);
    }

    public ResourceNotFound(String resourceName, String field, String value) {
        super(String.format("%s with %s - %s does not exist.", resourceName, field, value));
    }
}
