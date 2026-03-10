package com.example.demo.exception.customExceptions;

public class DatabaseOperationException extends RuntimeException {
    public DatabaseOperationException(String message) {
        super(message);
    }

    public DatabaseOperationException(String operation, String task) {
        super(String.format("Internal Server Error - Failed to %s %s", operation, task));
    }
}