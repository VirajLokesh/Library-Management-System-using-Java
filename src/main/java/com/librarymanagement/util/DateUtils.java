package com.librarymanagement.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtils {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public static final int DEFAULT_LOAN_PERIOD_DAYS = 14;
    
    public static final double PER_DAY_FINE = 10.0;
    
    public static final int MAX_ACTIVE_LOANS = 5;

    public static String formatDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DATE_FORMATTER);
    }

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

    public static LocalDate calculateDueDate(LocalDate issueDate) {
        return issueDate.plusDays(DEFAULT_LOAN_PERIOD_DAYS);
    }

    public static double calculateFine(LocalDate dueDate, LocalDate returnDate) {
        if (returnDate == null || returnDate.isBefore(dueDate) || returnDate.isEqual(dueDate)) {
            return 0.0;
        }
        long daysLate = java.time.temporal.ChronoUnit.DAYS.between(dueDate, returnDate);
        return daysLate * PER_DAY_FINE;
    }

    public static boolean isOverdue(LocalDate dueDate, LocalDate returnDate) {
        if (returnDate != null) {
            return returnDate.isAfter(dueDate);
        }
        return LocalDate.now().isAfter(dueDate);
    }
}
