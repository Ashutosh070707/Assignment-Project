package com.example.demo.exception.customExceptions;

public class InvalidArgumentException extends IllegalArgumentException {
    public InvalidArgumentException(String message) {
        super(message);
    }
}
