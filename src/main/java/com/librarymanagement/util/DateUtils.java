package com.librarymanagement.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility class for date operations.
 */
public class DateUtils {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    // Default loan period in days
    public static final int DEFAULT_LOAN_PERIOD_DAYS = 14;
    
    // Fine per day for overdue books
    public static final double PER_DAY_FINE = 10.0;
    
    // Maximum number of books a member can borrow at once
    public static final int MAX_ACTIVE_LOANS = 5;

    /**
     * Formats a LocalDate to string (yyyy-MM-dd).
     *
     * @param date the date to format
     * @return formatted date string
     */
    public static String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DATE_FORMATTER);
    }

    /**
     * Parses a date string (yyyy-MM-dd) to LocalDate.
     *
     * @param dateString the date string to parse
     * @return LocalDate object
     * @throws IllegalArgumentException if date format is invalid
     */
    public static LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            throw new IllegalArgumentException("Date string cannot be empty");
        }
        try {
            return LocalDate.parse(dateString.trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected yyyy-MM-dd", e);
        }
    }

    /**
     * Calculates the due date based on issue date and loan period.
     *
     * @param issueDate the issue date
     * @return the due date
     */
    public static LocalDate calculateDueDate(LocalDate issueDate) {
        return issueDate.plusDays(DEFAULT_LOAN_PERIOD_DAYS);
    }

    /**
     * Calculates the fine amount for an overdue book.
     *
     * @param dueDate the due date
     * @param returnDate the return date (or current date if not returned)
     * @return the fine amount
     */
    public static double calculateFine(LocalDate dueDate, LocalDate returnDate) {
        if (returnDate == null || returnDate.isBefore(dueDate) || returnDate.isEqual(dueDate)) {
            return 0.0;
        }
        long daysLate = java.time.temporal.ChronoUnit.DAYS.between(dueDate, returnDate);
        return daysLate * PER_DAY_FINE;
    }

    /**
     * Checks if a loan is overdue.
     *
     * @param dueDate the due date
     * @param returnDate the return date (null if not returned)
     * @return true if overdue, false otherwise
     */
    public static boolean isOverdue(LocalDate dueDate, LocalDate returnDate) {
        if (returnDate != null) {
            return returnDate.isAfter(dueDate);
        }
        return LocalDate.now().isAfter(dueDate);
    }
}

