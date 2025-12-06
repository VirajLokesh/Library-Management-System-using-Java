package com.librarymanagement.exception;

/**
 * Exception thrown when authentication fails (invalid username or password).
 */
public class AuthenticationException extends Exception {
    public AuthenticationException(String message) {
        super(message);
    }
}

