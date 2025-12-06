package com.librarymanagement.exception;

/**
 * Exception thrown when input validation fails.
 */
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}

