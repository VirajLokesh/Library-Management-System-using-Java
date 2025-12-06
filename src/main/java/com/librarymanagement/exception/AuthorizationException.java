package com.librarymanagement.exception;

/**
 * Exception thrown when a user tries to perform an action they are not authorized for.
 */
public class AuthorizationException extends Exception {
    public AuthorizationException(String message) {
        super(message);
    }
}

