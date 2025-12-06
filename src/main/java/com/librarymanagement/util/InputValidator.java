package com.librarymanagement.util;

import java.util.regex.Pattern;

/**
 * Utility class for input validation.
 * Provides methods to validate various types of input.
 */
public class InputValidator {
    // Email pattern
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    // Phone pattern (allows digits, spaces, hyphens, parentheses)
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[\\d\\s\\-\\(\\)]+$");
    
    // ISBN pattern (allows alphanumeric and hyphens)
    private static final Pattern ISBN_PATTERN = 
        Pattern.compile("^[0-9\\-X]+$");

    /**
     * Validates that a string is not null or empty.
     *
     * @param input the string to validate
     * @param fieldName the name of the field (for error messages)
     * @throws IllegalArgumentException if input is null or empty
     */
    public static void validateNotEmpty(String input, String fieldName) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
    }

    /**
     * Validates that an integer is positive.
     *
     * @param value the integer to validate
     * @param fieldName the name of the field (for error messages)
     * @throws IllegalArgumentException if value is not positive
     */
    public static void validatePositive(Integer value, String fieldName) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be a positive number");
        }
    }

    /**
     * Validates email format.
     *
     * @param email the email to validate
     * @throws IllegalArgumentException if email format is invalid
     */
    public static void validateEmail(String email) {
        validateNotEmpty(email, "Email");
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    /**
     * Validates phone number format.
     *
     * @param phone the phone number to validate
     * @throws IllegalArgumentException if phone format is invalid
     */
    public static void validatePhone(String phone) {
        validateNotEmpty(phone, "Phone");
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
    }

    /**
     * Validates ISBN format.
     *
     * @param isbn the ISBN to validate
     * @throws IllegalArgumentException if ISBN format is invalid
     */
    public static void validateISBN(String isbn) {
        validateNotEmpty(isbn, "ISBN");
        if (!ISBN_PATTERN.matcher(isbn).matches()) {
            throw new IllegalArgumentException("Invalid ISBN format");
        }
    }

    /**
     * Validates that a year is reasonable (between 1000 and current year + 10).
     *
     * @param year the year to validate
     * @throws IllegalArgumentException if year is out of range
     */
    public static void validateYear(Integer year) {
        if (year == null) {
            throw new IllegalArgumentException("Year cannot be null");
        }
        int currentYear = java.time.Year.now().getValue();
        if (year < 1000 || year > currentYear + 10) {
            throw new IllegalArgumentException("Year must be between 1000 and " + (currentYear + 10));
        }
    }
}

