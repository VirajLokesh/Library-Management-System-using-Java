package com.librarymanagement.exception;

/**
 * Exception thrown when an entity (User, Book, Member, Loan) is not found.
 */
public class EntityNotFoundException extends Exception {
    public EntityNotFoundException(String message) {
        super(message);
    }
}

