package com.librarymanagement.util;

import java.util.regex.Pattern;

public class InputValidator {
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[\\d\\s\\-\\(\\)]+$");
    
    private static final Pattern ISBN_PATTERN = 
        Pattern.compile("^[0-9\\-X]+$");

    public static void validateNotEmpty(String input, String fieldName) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
    }

    public static void validatePositive(Integer value, String fieldName) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be a positive number");
        }
    }

    public static void validateEmail(String email) {
        validateNotEmpty(email, "Email");
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    public static void validatePhone(String phone) {
        validateNotEmpty(phone, "Phone");
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new IllegalArgumentException("Invalid phone number format");
        }
    }

    public static void validateISBN(String isbn) {
        validateNotEmpty(isbn, "ISBN");
        if (!ISBN_PATTERN.matcher(isbn).matches()) {
            throw new IllegalArgumentException("Invalid ISBN format");
        }
    }

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
